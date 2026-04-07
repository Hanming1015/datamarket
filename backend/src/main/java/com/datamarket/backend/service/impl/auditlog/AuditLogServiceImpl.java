package com.datamarket.backend.service.impl.auditlog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.datamarket.backend.mapper.AuditLogMapper;
import com.datamarket.backend.pojo.AuditLog;
import com.datamarket.backend.service.auditlog.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    public List<AuditLog> getAuditLogs(String action, String userId, String datasetId, String from, String to) {
        QueryWrapper<AuditLog> queryWrapper = new QueryWrapper<>();

        if (action != null && !action.isEmpty() && !"all".equals(action)) {
            queryWrapper.eq("action", action);
        }
        if (userId != null && !userId.isEmpty()) {
            queryWrapper.eq("user_id", userId);
        }
        if (datasetId != null && !datasetId.isEmpty()) {
            queryWrapper.eq("dataset_id", datasetId);
        }
        queryWrapper.orderByDesc("timestamp");

        return auditLogMapper.selectList(queryWrapper);
    }
}
