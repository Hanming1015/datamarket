package com.datamarket.backend.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.datamarket.backend.dto.PricingResult;
import com.datamarket.backend.mapper.DatasetMapper;
import com.datamarket.backend.mapper.PricingConfigMapper;
import com.datamarket.backend.pojo.PricingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 核心算法②：定价计算引擎
 * 职责：根据同意的字段和数据库的定价配置，计算出本次访问的详细账单
 */
@Component
public class PricingEngine {

    @Autowired
    private PricingConfigMapper pricingConfigMapper;

    @Autowired
    private DatasetMapper datasetMapper;

    public PricingResult calculate(List<String> allowedFields, List<Map<String, Object>> fieldsSchema, String purpose, String consumerId, String datasetId) {
        
        // 1. 从数据库查出最新的定价配置
        QueryWrapper<PricingConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dataset_id", datasetId);
        PricingConfig config = pricingConfigMapper.selectOne(queryWrapper);

        if (config == null) {
            throw new RuntimeException("The system pricing configuration is missing, and thus billing cannot be performed.");
        }

        // 2. 识别敏感字段
        Set<String> sensitiveNames = new HashSet<>();
        if (fieldsSchema != null) {
            for (Map<String, Object> fieldObj : fieldsSchema) {
                if (Boolean.TRUE.equals(fieldObj.get("sensitive"))) {
                    sensitiveNames.add((String) fieldObj.get("name"));
                }
            }
        }

        // 3. 统计数量
        long normalCount = 0;
        long sensitiveCount = 0;
        for (String field : allowedFields) {
            if (sensitiveNames.contains(field)) {
                sensitiveCount++;
            } else {
                normalCount++;
            }
        }

        // 4. 计算基础金额
        BigDecimal baseCost = config.getPerAccessBase() != null ? config.getPerAccessBase() : BigDecimal.ZERO;
        
        BigDecimal perField = config.getPerField() != null ? config.getPerField() : BigDecimal.ZERO;
        BigDecimal normalCost = perField.multiply(new BigDecimal(normalCount));
        
        BigDecimal sensitiveMultiplier = config.getSensitiveFieldMultiplier() != null ? config.getSensitiveFieldMultiplier() : BigDecimal.ONE;
        BigDecimal sensitiveCost = perField.multiply(sensitiveMultiplier).multiply(new BigDecimal(sensitiveCount));

        // 5. 获取用途倍率 
        BigDecimal purposeMultiplier = BigDecimal.ONE;
        if (config.getPurposeMultiplierJson() != null && config.getPurposeMultiplierJson().containsKey(purpose)) {
            Object rawMult = config.getPurposeMultiplierJson().get(purpose);
            purposeMultiplier = new BigDecimal(rawMult.toString());
        }

        // 6. 获取批量折扣：采用本次实际请求的所有合法字段总数
        int historyCount = allowedFields != null ? allowedFields.size() : 0; 
        BigDecimal bulkDiscount = calculateBulkDiscount(historyCount, config.getBulkDiscountJson());

        // 7. 大公式计算
        // 字段金额 = (普通字段费 + 敏感字段费) * 批量数量折扣
        BigDecimal fieldsTotal = normalCost.add(sensitiveCost).multiply(bulkDiscount);
        
        // 总金额 = (基础访问费 + 折扣后的字段金额) * 取数用途倍率
        BigDecimal total = baseCost.add(fieldsTotal).multiply(purposeMultiplier);
        total = total.setScale(2, RoundingMode.HALF_UP);

        return new PricingResult(baseCost, normalCost, sensitiveCost, purposeMultiplier, bulkDiscount, total);
    }

    private BigDecimal calculateBulkDiscount(int count, Map<?, ?> tiers) {
        if (tiers == null || tiers.isEmpty()) return BigDecimal.ONE;
        
        BigDecimal bestDiscount = BigDecimal.ONE;
        int highestTier = 0;
        
        for (Map.Entry<?, ?> entry : tiers.entrySet()) {
            try {
                int tierKey = Integer.parseInt(entry.getKey().toString());
                BigDecimal discount = new BigDecimal(entry.getValue().toString());
                if (count >= tierKey && tierKey > highestTier) {
                    highestTier = tierKey;
                    bestDiscount = discount;
                }
            } catch (Exception e) {
                // 忽略解析错误的节点
            }
        }
        return bestDiscount;
    }
}
