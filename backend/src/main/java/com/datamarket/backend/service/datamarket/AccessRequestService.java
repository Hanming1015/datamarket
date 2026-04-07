package com.datamarket.backend.service.datamarket;

import com.datamarket.backend.dto.DataAccessRequest;

import java.util.Map;

public interface AccessRequestService {
    Map<String, Object> processAccessRequest(DataAccessRequest request);
}
