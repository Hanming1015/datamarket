package com.datamarket.backend.service.auditlog;

import com.datamarket.backend.pojo.ConsentRule;

import java.util.Map;

public interface ConsentRuleService {
    ConsentRule createConsent(Map<String, Object> body);
}
