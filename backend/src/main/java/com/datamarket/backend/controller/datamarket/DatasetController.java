package com.datamarket.backend.controller.datamarket;

import com.datamarket.backend.service.datamarket.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

    @GetMapping("/api/datasets")
    public ResponseEntity<?> getDatasets(
            @RequestParam(required = false) String ownerId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(datasetService.getDatasetList(ownerId, category, keyword));
    }
}
