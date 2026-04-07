package com.datamarket.backend.service.impl.consentmanagement;

import com.datamarket.backend.mapper.ConsentRuleMapper;
import com.datamarket.backend.pojo.AuditLog;
import com.datamarket.backend.pojo.ConsentRule;
import com.datamarket.backend.service.auditlog.AuditLogService;
import com.datamarket.backend.service.auditlog.ConsentRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ConsentRuleServiceImpl implements ConsentRuleService {

    @Autowired
    private ConsentRuleMapper consentRuleMapper;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    @Transactional
    public ConsentRule createConsentRule(Map<String, Object> body) {
        //System.out.println("Body: " + body);

        ConsentRule consentRule = new ConsentRule();

        String datasetId = body.get("datasetId").toString();
        consentRule.setId(UUID.randomUUID().toString());
        consentRule.setDatasetId(datasetId);

        consentRule.setAllowedRoles((List<String>) body.get("allowedRoles"));
        consentRule.setAllowedPurposes((List<String>) body.get("allowedPurposes"));
        consentRule.setAllowedFields((List<String>) body.get("allowedFields"));

        if (body.containsKey("deniedFields")) {
            consentRule.setDeniedFields((List<String>) body.get("deniedFields"));
        } else {
            consentRule.setDeniedFields(List.of());
        }

        consentRule.setValidFrom(LocalDate.now());
        consentRule.setValidUntil(LocalDate.parse((String) body.get("validUntil")));
        consentRule.setStatus("active");
        consentRule.setCreatedAt(LocalDateTime.now());

        consentRuleMapper.insert(consentRule);

        //Silently record an audit log.
        auditLogService.addAuditLog("owner1", "consent_created", consentRule.getDatasetId(), "Created consent for roles: " + String.join(",", consentRule.getAllowedRoles()));

        return consentRule;
    }

    @Override
    public void revokeConsentRule(String id) {
        ConsentRule consentRule = consentRuleMapper.selectById(id);

        if (consentRule == null) {
            throw new RuntimeException("No such consent rule！ID: " + id);
        }

        consentRule.setStatus("revoked");

        consentRule.setRevokedAt(LocalDateTime.now());

        consentRuleMapper.updateById(consentRule);

        //Silently record an audit log.
        auditLogService.addAuditLog("owner1", "consent_revoked", consentRule.getDatasetId(), "Revoked consent for roles: " + String.join(",", consentRule.getAllowedRoles()));
    }

    @Override
    public List<ConsentRule> getConsentRules() {
        return consentRuleMapper.selectList(null);
    }

}
