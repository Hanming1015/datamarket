package com.datamarket.backend.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.datamarket.backend.dto.DataAccessRequest;
import com.datamarket.backend.mapper.ConsentRuleMapper;
import com.datamarket.backend.pojo.ConsentRule;
import com.datamarket.backend.dto.MatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class ConsentMatchingEngine {

    @Autowired
    private ConsentRuleMapper consentRuleMapper;

    public MatchResult match(DataAccessRequest request) {

        // 1. 从数据库查出该数据集所有状态为 "active" 的同意规则
        QueryWrapper<ConsentRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dataset_id", request.getDatasetId())
                .eq("status", "active");
        List<ConsentRule> activeRules = consentRuleMapper.selectList(queryWrapper);

        // 2. 过滤：日期是否在有效期内
        LocalDate now = LocalDate.now();
        List<ConsentRule> validRules = activeRules.stream()
                .filter(r -> !now.isBefore(r.getValidFrom()) && !now.isAfter(r.getValidUntil()))
                .collect(Collectors.toList());

        if (validRules.isEmpty()) {
            return MatchResult.deny("No active and valid consent rules for this dataset");
        }

        // 3. 过滤：校验 Role (consumerType)
        validRules = validRules.stream()
                .filter(r -> r.getAllowedRoles() != null && r.getAllowedRoles().contains(request.getConsumerType()))
                .collect(Collectors.toList());

        if (validRules.isEmpty()) {
            return MatchResult.deny("Consumer type not authorized");
        }

        // 4. 过滤：校验 Purpose
        validRules = validRules.stream()
                .filter(r -> r.getAllowedPurposes() != null && r.getAllowedPurposes().contains(request.getPurpose()))
                .collect(Collectors.toList());

        if (validRules.isEmpty()) {
            return MatchResult.deny("Purpose not authorized");
        }

        // --- 进入核心：字段级碰撞算法 ---
        // 5. 提取剩余存活规则的字段并集（拒绝字段优先）
        Set<String> allAllowed = validRules.stream()
                .filter(r -> r.getAllowedFields() != null)
                .flatMap(r -> r.getAllowedFields().stream())
                .collect(Collectors.toSet());

        Set<String> allDenied = validRules.stream()
                .filter(r -> r.getDeniedFields() != null)
                .flatMap(r -> r.getDeniedFields().stream())
                .collect(Collectors.toSet());

        List<String> finalAllowed = new ArrayList<>();
        List<String> finalDenied = new ArrayList<>();
        Map<String, String> reasons = new LinkedHashMap<>();

        // 6. 逐一判定请求申请的字段
        for (String field : request.getRequestedFields()) {
            if (allDenied.contains(field)) {
                // 如果命中黑名单，一票否决
                finalDenied.add(field);
                reasons.put(field, "Field explicitly denied by owner");
            } else if (allAllowed.contains(field)) {
                // 如果在白名单内，放行
                finalAllowed.add(field);
            } else {
                // 既不在白名单也不在黑名单，默认拒绝（白名单机制）
                finalDenied.add(field);
                reasons.put(field, "Field not in consent allowed list");
            }
        }

        // 7. 取所有生效规则里，最早过期的那一天，作为本次授权的到期日
        LocalDate earliestExpiry = validRules.stream()
                .map(ConsentRule::getValidUntil)
                .min(LocalDate::compareTo)
                .orElse(now);

        // 8. 判定最终大结论
        if (finalAllowed.isEmpty()) {
            return MatchResult.deny("No requested fields are authorized");
        } else if (finalDenied.isEmpty()) {
            return MatchResult.permit(finalAllowed, earliestExpiry);
        } else {
            return MatchResult.partial(finalAllowed, finalDenied, reasons, earliestExpiry);
        }
    }
}
