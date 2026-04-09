package com.datamarket.backend.service.user.dataset.pricingconfig;

import com.datamarket.backend.pojo.PricingConfig;

import java.util.List;

public interface PricingConfigService {
    PricingConfig addPricingConfig(PricingConfig pricingConfig);

    PricingConfig  getPricingConfig(String datasetId);

    PricingConfig updatePricingConfig(PricingConfig pricingConfig);

    void removePricingConfig(String id);
}
