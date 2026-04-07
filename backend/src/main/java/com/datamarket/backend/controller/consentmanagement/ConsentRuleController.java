package com.datamarket.backend.controller.consentmanagement;

import com.datamarket.backend.pojo.ConsentRule;
import com.datamarket.backend.service.auditlog.ConsentRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConsentRuleController {

    @Autowired
    private ConsentRuleService consentRuleService;

    @PostMapping("/api/consents")
    public ResponseEntity<?> createConsent (@RequestBody Map<String, Object> body) {
        ConsentRule consentRule = consentRuleService.createConsent(body);
        return ResponseEntity.ok(consentRule);
    }
}
