package com.datamarket.backend.service.auditlog;

import com.baomidou.mybatisplus.extension.service.IService;
import com.datamarket.backend.pojo.AuditLog;
import java.util.List;

public interface AuditLogService {
    List<AuditLog> getAuditLogs(String action, String userId, String datasetId, String from, String to);
}