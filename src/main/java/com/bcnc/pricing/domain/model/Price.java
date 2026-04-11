package com.bcnc.pricing.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Price {

    private final Long brandId;
    private final Long productId;
    private final Integer priceList;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer priority;
    private final BigDecimal amount;
    private final String currency;
}
