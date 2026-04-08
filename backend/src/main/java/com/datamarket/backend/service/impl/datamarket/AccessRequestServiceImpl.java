package com.datamarket.backend.service.impl.datamarket;

import com.datamarket.backend.dto.DataAccessRequest;
import com.datamarket.backend.utils.PricingResult;
import com.datamarket.backend.engine.ConsentMatchingEngine;
import com.datamarket.backend.engine.PricingEngine;
import com.datamarket.backend.mapper.AccessRequestMapper;
import com.datamarket.backend.pojo.AccessRequest;
import com.datamarket.backend.service.auditlog.AuditLogService;
import com.datamarket.backend.service.datamarket.AccessRequestService;
import com.datamarket.backend.utils.MatchResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AccessRequestServiceImpl implements AccessRequestService {

    @Autowired
    private ConsentMatchingEngine matchingEngine;

    @Autowired
    private PricingEngine pricingEngine;

    @Autowired
    private AccessRequestMapper accessRequestMapper;

    // 假设你有以下依赖，如果没有可以先注释掉跑通主流程
    // @Autowired private DatasetMapper datasetMapper;
    // @Autowired private BillingRecordMapper billingRecordMapper;

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
    @Transactional(rollbackFor = Exception.class)  // 读写多张表，必须开启事务
    public Map<String, Object> processAccessRequest(DataAccessRequest request) {

        // 1. 查数据集信息 (这里我用模拟数据代替 datasetMapper 的查询)
        // Dataset dataset = datasetMapper.selectById(request.getDatasetId());

        // --- 核心第一步：匹配引擎判决 ---
        MatchResult match = matchingEngine.match(request);

        // 2. 组装要存库的 POJO 胖子
        AccessRequest ar = new AccessRequest();
        ar.setId(UUID.randomUUID().toString());
        ar.setDatasetId(request.getDatasetId());
        // ar.setDatasetName(dataset.getName());
        ar.setDatasetName("模拟数据集名称");
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

            // 存访问记录表
            accessRequestMapper.insert(ar);

            // 存审计日志
            auditLogService.addAuditLog(request.getRequesterId(), "request_rejected", request.getDatasetId(), "Rejected: " + match.getDenyReason());
        } else {
            // 分支 B：完全通过 / 部分通过
            ar.setStatus(match.getDecision()); // 将被设为 "approved" 或 "partial"
            ar.setAllowedFields(match.getAllowedFields());
            ar.setDeniedFields(match.getDeniedFields());
            ar.setDenialReasons(match.getReasons());
            ar.setRespondedAt(LocalDateTime.now());

            // --- 核心第二步：定价引擎算钱 ---
            // 传入的 schema 先用 null 代替，你可以改成 dataset.getFieldsSchema()
            pricing = pricingEngine.calculate(match.getAllowedFields(), null, request.getPurpose(), request.getRequesterId());
            ar.setCost(pricing.getTotalCost());

            // 存访问记录表
            accessRequestMapper.insert(ar);

            // 【计费逻辑】如果建了 BillingRecord 表，在这里生成账单并 insert
            // BillingRecord bill = new BillingRecord(...);
            // billingRecordMapper.insert(bill);

            // 存审计日志
            auditLogService.addAuditLog(request.getRequesterId(), "data_accessed", request.getDatasetId(),
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