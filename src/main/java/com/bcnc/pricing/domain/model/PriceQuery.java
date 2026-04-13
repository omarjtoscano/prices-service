package com.bcnc.pricing.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;

/**
 * Value object representing the input criteria for a price lookup.
 * Encapsulates the application date, product, and brand to query.
 */
@Getter
public class PriceQuery {

    private final LocalDateTime applicationDate;
    private final Long productId;
    private final Long brandId;

    public PriceQuery(LocalDateTime applicationDate, Long productId, Long brandId) {
        this.applicationDate = Objects.requireNonNull(applicationDate, "applicationDate must not be null");
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.brandId = Objects.requireNonNull(brandId, "brandId must not be null");
    }
}
