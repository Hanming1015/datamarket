package com.datamarket.backend.service.impl.billing;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.datamarket.backend.mapper.BillingRecordMapper;
import com.datamarket.backend.mapper.DatasetMapper;
import com.datamarket.backend.pojo.BillingRecord;
import com.datamarket.backend.pojo.Dataset;
import com.datamarket.backend.service.billing.BillingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillingRecordServiceImpl implements BillingRecordService {

    @Autowired
    private BillingRecordMapper billingRecordMapper;

    @Autowired
    private DatasetMapper datasetMapper;

    @Override
    public List<BillingRecord> getBillingSummary(String userId, String role) {

        QueryWrapper<BillingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");

        if ("consumer".equals(role)) {
            queryWrapper.eq("user_id", userId);
            return billingRecordMapper.selectList(queryWrapper);

        } else if ("owner".equals(role)) {
            QueryWrapper<Dataset> dsQuery = new QueryWrapper<>();
            dsQuery.eq("owner_id", userId);
            dsQuery.select("id");
            List<Dataset> myDatasets = datasetMapper.selectList(dsQuery);

            if (myDatasets == null || myDatasets.isEmpty()) {
                return List.of();
            }

            List<String> myDatasetIds = myDatasets.stream()
                    .map(Dataset::getId)
                    .collect(Collectors.toList());

            queryWrapper.in("dataset_id", myDatasetIds);
            return billingRecordMapper.selectList(queryWrapper);
            
        } else {
            throw new IllegalArgumentException("Invalid role specified. Must be 'owner' or 'consumer'.");
        }
    }
}
