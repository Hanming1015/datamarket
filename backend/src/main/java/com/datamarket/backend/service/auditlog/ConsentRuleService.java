package com.datamarket.backend.service.auditlog;

import com.datamarket.backend.pojo.ConsentRule;

import java.util.List;
import java.util.Map;

public interface ConsentRuleService {
    ConsentRule createConsentRule(Map<String, Object> body);

    void revokeConsentRule(String id);

    List<ConsentRule> getConsentRules();
}
