package com.datamarket.backend.service.impl.datamarket;

import com.datamarket.backend.dto.DataAccessRequest;
import com.datamarket.backend.dto.PricingResult;
import com.datamarket.backend.engine.ConsentMatchingEngine;
import com.datamarket.backend.engine.PricingEngine;
import com.datamarket.backend.mapper.AccessRequestMapper;
import com.datamarket.backend.mapper.BillingRecordMapper;
import com.datamarket.backend.mapper.DatasetMapper;
import com.datamarket.backend.pojo.AccessRequest;
import com.datamarket.backend.pojo.BillingRecord;
import com.datamarket.backend.pojo.Dataset;
import com.datamarket.backend.pojo.User;
import com.datamarket.backend.service.auditlog.AuditLogService;
import com.datamarket.backend.service.datamarket.AccessRequestService;
import com.datamarket.backend.dto.MatchResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.datamarket.backend.utils.SecurityUtil;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccessRequestServiceImpl implements AccessRequestService {

    @Autowired
    private ConsentMatchingEngine matchingEngine;

    @Autowired
    private PricingEngine pricingEngine;

    @Autowired
    private AccessRequestMapper accessRequestMapper;

    @Autowired private DatasetMapper datasetMapper;

    @Autowired private BillingRecordMapper billingRecordMapper;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public List<AccessRequest> getAccessRequests(String userId, String datasetId) {
        QueryWrapper<AccessRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("requested_at");

        if (StringUtils.hasText(userId)) {
            queryWrapper.eq("requester_id", userId);
        }

        if (StringUtils.hasText(datasetId)) {
            queryWrapper.eq("dataset_id", datasetId);
        }

        return accessRequestMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> processAccessRequest(DataAccessRequest request) {

        Dataset dataset = datasetMapper.selectById(request.getDatasetId());
        String datasetName = (dataset != null) ? dataset.getName() : "Unknown Dataset";
        List<Map<String, Object>> fieldsSchema = (dataset != null) ? dataset.getFieldsSchema() : new ArrayList<>();

        // 1. 匹配引擎判决
        MatchResult match = matchingEngine.match(request);

        User user = SecurityUtil.getCurrentUser();

        // 2. 组装要存库的 POJO
        AccessRequest ar = new AccessRequest();
        ar.setId(UUID.randomUUID().toString());
        ar.setDatasetId(request.getDatasetId());
        ar.setDatasetName(datasetName);
        ar.setRequesterId(request.getRequesterId());
        ar.setRequesterName(request.getRequesterName());
        ar.setConsumerType(request.getConsumerType());
        ar.setPurpose(request.getPurpose());
        ar.setRequestedFields(request.getRequestedFields());
        ar.setRequestedAt(LocalDateTime.now());

        Map<String, Object> response = new LinkedHashMap<>();
        PricingResult pricing = null;

        // 3. 根据判决结果走分支
        if ("rejected".equals(match.getDecision())) {
            // 分支 A：被拉黑彻底拒绝
            ar.setStatus("rejected");
            ar.setRespondedAt(LocalDateTime.now());
            ar.setCost(BigDecimal.ZERO);

            accessRequestMapper.insert(ar);

            auditLogService.addAuditLog(user.getId(), user.getUsername(), "request_rejected", request.getDatasetId(), datasetName,"Rejected: " + match.getDenyReason());
        } else {
            // 分支 B：完全通过 / 部分通过
            ar.setStatus(match.getDecision()); // 将被设为 "approved" 或 "partial"
            ar.setAllowedFields(match.getAllowedFields());
            ar.setDeniedFields(match.getDeniedFields());
            ar.setDenialReasons(match.getReasons());
            ar.setRespondedAt(LocalDateTime.now());

            // --- 核心第二步：定价引擎算钱 ---
            pricing = pricingEngine.calculate(match.getAllowedFields(), fieldsSchema, request.getPurpose(), request.getRequesterId());
            ar.setCost(pricing.getTotalCost());

            // 存访问记录表
            accessRequestMapper.insert(ar);

            // 【计费逻辑】如果建了 BillingRecord 表，在这里生成账单并 insert
//            BillingRecord bill = new BillingRecord();
//
//            bill.setAccessRequestId(ar.getId()); // 关联刚才生成的请求记录ID
//            bill.setDatasetId(ar.getDatasetId());
//            bill.setBuyerId(ar.getRequesterId()); // 买方：发起请求的人
//            bill.setSellerId(dataset != null ? dataset.getOwnerId() : null); // 卖方：数据集的拥有者
//
//            // 直接使用上面 pricingEngine 计算出来的最终总价
//            bill.setAmount(pricing.getTotalCost());
//
//            // 初始账单状态，如果是后付费/待支付模式则设为 PENDING
//            // 如果你希望请求一通过就立刻算是交易完成，那就是 COMPLETED
//            bill.setStatus("PENDING");
//
//            bill.setCreatedAt(LocalDateTime.now());

            // 可选：把价格引擎算出来的明细存进备注字段（如果你有这个字段的话，方便以后对账）
            // bill.setPricingDetails(String.format("Base: %s, Mult: %s, Total: %s",
            //     pricing.getBaseCost(), pricing.getPurposeMultiplier(), pricing.getTotalCost()));

            // 插入数据库
//            billingRecordMapper.insert(bill);

            // 存审计日志
            auditLogService.addAuditLog(user.getId(), user.getUsername(), "data_accessed", request.getDatasetId(), datasetName,
                    "Fields allowed: " + match.getAllowedFields().size() + " | Cost: $" + pricing.getTotalCost());
        }

        // ==========================================
        // 4. 重头戏：组装不用写 VO 的纯手工豪华 JSON
        // ==========================================
        response.put("id", ar.getId());
        response.put("datasetId", ar.getDatasetId());
        response.put("datasetName", ar.getDatasetName());
        response.put("requesterId", ar.getRequesterId());
        response.put("requesterName", ar.getRequesterName());
        response.put("purpose", ar.getPurpose());
        response.put("requestedFields", ar.getRequestedFields());
        response.put("status", ar.getStatus());
        response.put("requestedAt", ar.getRequestedAt().toString());
        response.put("respondedAt", ar.getRespondedAt().toString());

        if (pricing != null) {
            // 在返回值里塞一个字典专门讲匹配原因
            response.put("decision", Map.of(
                    "allowedFields", match.getAllowedFields(),
                    "deniedFields", match.getDeniedFields(),
                    "reasons", match.getReasons()
            ));
            // 在返回值里塞一个字典专门讲价格明细
            response.put("pricing", Map.of(
                    "baseCost", pricing.getBaseCost(),
                    "fieldCost", pricing.getFieldCost(),
                    "sensitiveFieldCost", pricing.getSensitiveFieldCost(),
                    "purposeMultiplier", pricing.getPurposeMultiplier(),
                    "bulkDiscount", pricing.getBulkDiscount(),
                    "totalCost", pricing.getTotalCost()
            ));
        }

        return response;
    }
}