package com.bcnc.pricing.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain model representing an applicable price for a product within a brand.
 * Contains the monetary amount, currency, validity period, and priority level
 * used for price resolution when multiple prices overlap.
 */
@Getter
public class Price {

    private final Long brandId;
    private final Long productId;
    private final Integer priceList;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer priority;
    private final BigDecimal amount;
    private final String currency;

    @Builder
    private Price(Long brandId, Long productId, Integer priceList,
                  LocalDateTime startDate, LocalDateTime endDate,
                  Integer priority, BigDecimal amount, String currency) {
        this.brandId = Objects.requireNonNull(brandId, "brandId must not be null");
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.priceList = Objects.requireNonNull(priceList, "priceList must not be null");
        this.startDate = Objects.requireNonNull(startDate, "startDate must not be null");
        this.endDate = Objects.requireNonNull(endDate, "endDate must not be null");
        this.priority = Objects.requireNonNull(priority, "priority must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate");
        }
    }
}
