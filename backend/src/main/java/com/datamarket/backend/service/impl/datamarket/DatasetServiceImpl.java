package com.datamarket.backend.service.impl.datamarket;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.datamarket.backend.mapper.DatasetMapper;
import com.datamarket.backend.pojo.Dataset;
import com.datamarket.backend.service.datamarket.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private DatasetMapper datasetMapper;


    @Override
    public List<Dataset> getDatasetList(String ownerId, String category, String keyword) {

        QueryWrapper<Dataset> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");

        if (StringUtils.hasText(ownerId)) {
            queryWrapper.eq("owner_id", ownerId);
            return datasetMapper.selectList(queryWrapper);
        }
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
            return datasetMapper.selectList(queryWrapper);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword).or().like("description", keyword);
            return datasetMapper.selectList(queryWrapper);
        }

        return datasetMapper.selectList(queryWrapper);
    }
}
