package com.datamarket.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class DataAccessRequest {
    private String requesterId;      // 请求者ID (e.g., "req1")
    private String requesterName;    // 请求者名称 (e.g., "Stanford Medical")
    private String consumerType;     // 消费者类型/角色 (e.g., "Research Institution")
    private String datasetId;        // 目标数据集ID (e.g., "ds1")
    private String purpose;          // 申请目的 (e.g., "Clinical Trials")
    private List<String> requestedFields; // 申请访问的字段列表
}
