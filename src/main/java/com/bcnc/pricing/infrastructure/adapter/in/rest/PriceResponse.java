package com.bcnc.pricing.infrastructure.adapter.in.rest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PriceResponse {

    private final Long productId;
    private final Long brandId;
    private final Integer priceList;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final BigDecimal price;
    private final String curr; 
}
