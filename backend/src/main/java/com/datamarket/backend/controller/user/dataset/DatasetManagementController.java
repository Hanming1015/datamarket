package com.datamarket.backend.controller.user.dataset;


import com.datamarket.backend.pojo.Dataset;
import com.datamarket.backend.service.user.dataset.AddDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/dataset")
public class DatasetManagementController {

    @Autowired
    private AddDatasetService addDatasetService;

    @PostMapping("/add")
    public ResponseEntity<?> addDataset(@RequestBody Map<String, Object> body) {

        String name = (String) body.get("name");
        String description = (String) body.get("description");
        String category = (String) body.get("category");
        Integer recordCount = (Integer) body.get("recordCount");

        List<Map<String, Object>> fieldsSchema = (List<Map<String, Object>>) body.get("fieldsSchema");

        if (name == null || name.trim().isEmpty() || category == null || fieldsSchema == null || fieldsSchema.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Dataset name, category, and minimum 1 field schema are required."));
        }

        Dataset newDataset = addDatasetService.addDataset(name, description, category, recordCount, fieldsSchema);

        return ResponseEntity.ok(Map.of(
                "message", "Dataset created successfully!",
                "dataset", newDataset
        ));
    }
}
