package com.bcnc.pricing.infrastructure.adapter.out.persistence;

import com.bcnc.pricing.domain.model.Price;

public class PriceEntityMapper {

    private PriceEntityMapper() {
    }

    public static Price toDomain(PriceEntity entity) {
        return Price.builder()
                .brandId(entity.getBrandId())
                .productId(entity.getProductId())
                .priceList(entity.getPriceList())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .priority(entity.getPriority())
                .amount(entity.getPrice())
                .currency(entity.getCurr())
                .build();
    }
}
