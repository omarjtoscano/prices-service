package com.bcnc.pricing.infrastructure.adapter.in.rest;

import com.bcnc.pricing.domain.model.Price;

public class PriceResponseMapper {

    private PriceResponseMapper() {
    }

    public static PriceResponse toResponse(Price price) {
        return PriceResponse.builder()
                .productId(price.getProductId())
                .brandId(price.getBrandId())
                .priceList(price.getPriceList())
                .startDate(price.getStartDate())
                .endDate(price.getEndDate())
                .amount(price.getAmount())
                .currency(price.getCurrency())
                .build();
    }
}
