package com.datamarket.backend.engine;

import com.datamarket.backend.dto.PricingResult;
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

    // 完整的字段 schema 通常附着在 Dataset 上，这里简化直接传 schema (List<Map<String, Object>>)
    public PricingResult calculate(List<String> allowedFields, List<Map<String, Object>> fieldsSchema, String purpose, String consumerId) {
        
        // 1. 从数据库查出最新的定价配置 (拿 id=1 兜底)
        PricingConfig config = pricingConfigMapper.selectById(1);
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

        // 4. 计算基础金额 (使用 BigDecimal)
        BigDecimal baseCost = config.getPerAccessBase() != null ? config.getPerAccessBase() : BigDecimal.ZERO;
        
        BigDecimal perField = config.getPerField() != null ? config.getPerField() : BigDecimal.ZERO;
        BigDecimal normalCost = perField.multiply(new BigDecimal(normalCount));
        
        BigDecimal sensitiveMultiplier = config.getSensitiveFieldMultiplier() != null ? config.getSensitiveFieldMultiplier() : BigDecimal.ONE;
        BigDecimal sensitiveCost = perField.multiply(sensitiveMultiplier).multiply(new BigDecimal(sensitiveCount));

        // 5. 获取用途倍率 
        BigDecimal purposeMultiplier = BigDecimal.ONE;
        if (config.getPurposeMultiplierJson() != null && config.getPurposeMultiplierJson().containsKey(purpose)) {
            purposeMultiplier = config.getPurposeMultiplierJson().get(purpose);
        }

        // 6. 获取批量折扣 (假设目前 count=0)
        int historyCount = 0; 
        BigDecimal bulkDiscount = calculateBulkDiscount(historyCount, config.getBulkDiscountJson());

        // 7. 大公式计算
        BigDecimal total = baseCost.add(normalCost).add(sensitiveCost);
        total = total.multiply(purposeMultiplier).multiply(bulkDiscount);
        total = total.setScale(2, RoundingMode.HALF_UP);

        return new PricingResult(baseCost, normalCost, sensitiveCost, purposeMultiplier, bulkDiscount, total);
    }

    private BigDecimal calculateBulkDiscount(int count, Map<Integer, BigDecimal> tiers) {
        if (tiers == null || tiers.isEmpty()) return BigDecimal.ONE;
        
        BigDecimal bestDiscount = BigDecimal.ONE;
        int highestTier = 0;
        
        for (Map.Entry<Integer, BigDecimal> entry : tiers.entrySet()) {
            if (count >= entry.getKey() && entry.getKey() > highestTier) {
                highestTier = entry.getKey();
                bestDiscount = entry.getValue();
            }
        }
        return bestDiscount;
    }
}
