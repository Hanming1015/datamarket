package com.datamarket.backend.service.billing;

import com.datamarket.backend.pojo.BillingRecord;

import java.util.List;

public interface BillingRecordService {
    List<BillingRecord> getBillingSummary(String userId, String role);
}
