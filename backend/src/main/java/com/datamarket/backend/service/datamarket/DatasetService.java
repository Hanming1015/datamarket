package com.datamarket.backend.service.datamarket;

import com.datamarket.backend.pojo.Dataset;

import java.util.List;

public interface DatasetService {
    List<Dataset> getDatasetList(String ownerId, String category, String keyword);
}
