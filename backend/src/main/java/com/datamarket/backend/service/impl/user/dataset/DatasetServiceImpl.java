package com.datamarket.backend.service.impl.user.dataset;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.datamarket.backend.mapper.DatasetMapper;
import com.datamarket.backend.pojo.Dataset;
import com.datamarket.backend.pojo.User;
import com.datamarket.backend.service.impl.utils.UserDetailsImpl;
import com.datamarket.backend.service.user.dataset.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private DatasetMapper datasetMapper;

    private User getCurrentUser() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        return loginUser.getUser();
    }

    @Override
    public Dataset addDataset(Dataset dataset) {
        User user = getCurrentUser();

        if (dataset.getFieldsSchema() != null) {
            List<String> fieldNames = dataset.getFieldsSchema().stream()
                    .map(schema -> (String) schema.get("name"))
                    .collect(Collectors.toList());
            dataset.setFields(fieldNames);
        }

        dataset.setOwnerId(user.getId());
        dataset.setCreatedAt(LocalDateTime.now());

        datasetMapper.insert(dataset);
        return dataset;
    }

    @Override
    public List<Dataset> getDatasetList() {
        User user = getCurrentUser();
        QueryWrapper<Dataset> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", user.getId()); // 只查自己的
        return datasetMapper.selectList(queryWrapper);
    }

    @Override
    public Dataset updateDataset(Dataset dataset) {
        datasetMapper.updateById(dataset);
        return datasetMapper.selectById(dataset.getId());
    }

    @Override
    public void removeDataset(String id) {
        datasetMapper.deleteById(id);
    }
}
