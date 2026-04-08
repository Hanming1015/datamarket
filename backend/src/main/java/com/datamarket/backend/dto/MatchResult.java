package com.datamarket.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class MatchResult {
    private String decision;  // "approved" | "partial" | "rejected"
    private List<String> allowedFields;
    private List<String> deniedFields;
    private Map<String, String> reasons;
    private LocalDate consentExpiresAt;
    private String denyReason;

    // 静态工厂方法（完全通过）
    public static MatchResult permit(List<String> allowed, LocalDate expiry) {
        MatchResult r = new MatchResult();
        r.decision = "approved";
        r.allowedFields = allowed;
        r.deniedFields = List.of();
        r.reasons = Map.of();
        r.consentExpiresAt = expiry;
        return r;
    }

    // 静态工厂方法（部分通过）
    public static MatchResult partial(List<String> allowed, List<String> denied,
                                      Map<String, String> reasons, LocalDate expiry) {
        MatchResult r = new MatchResult();
        r.decision = "partial";
        r.allowedFields = allowed;
        r.deniedFields = denied;
        r.reasons = reasons;
        r.consentExpiresAt = expiry;
        return r;
    }

    // 静态工厂方法（彻底拒绝）
    public static MatchResult deny(String reason) {
        MatchResult r = new MatchResult();
        r.decision = "rejected";
        r.denyReason = reason;
        r.allowedFields = List.of();
        r.deniedFields = List.of();
        r.reasons = Map.of();
        return r;
    }
}