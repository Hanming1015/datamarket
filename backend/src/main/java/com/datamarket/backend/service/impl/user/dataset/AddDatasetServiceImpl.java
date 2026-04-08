package com.datamarket.backend.service.impl.user.dataset;

import com.datamarket.backend.mapper.DatasetMapper;
import com.datamarket.backend.pojo.Dataset;
import com.datamarket.backend.pojo.User;
import com.datamarket.backend.service.impl.utils.UserDetailsImpl;
import com.datamarket.backend.service.user.dataset.AddDatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AddDatasetServiceImpl implements AddDatasetService {

    @Autowired
    private DatasetMapper datasetMapper;

    @Override
    public Dataset addDataset(String name, String description, String category, Integer recordCount, List<Map<String, Object>> fieldsSchema) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        List<String> fieldNames = fieldsSchema.stream()
                .map(schema -> (String) schema.get("name"))
                .collect(Collectors.toList());

        Dataset dataset = new Dataset();
        dataset.setName(name);
        dataset.setDescription(description);
        dataset.setCategory(category);
        dataset.setRecordCount(recordCount);
        dataset.setFieldsSchema(fieldsSchema);
        dataset.setFields(fieldNames);
        dataset.setOwnerId(user.getId());
        dataset.setCreatedAt(LocalDateTime.now());
        datasetMapper.insert(dataset);
        return null;
    }
}
