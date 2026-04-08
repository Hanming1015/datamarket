package com.datamarket.backend.service.user.dataset;

import com.datamarket.backend.pojo.Dataset;

import java.util.List;
import java.util.Map;

public interface AddDatasetService {
    Dataset addDataset(String name, String description, String category, Integer recordCount, List<Map<String, Object>> fieldsSchema);
}
