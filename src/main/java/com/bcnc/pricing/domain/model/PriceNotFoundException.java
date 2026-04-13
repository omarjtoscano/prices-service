package com.bcnc.pricing.domain.model;

import java.time.LocalDateTime;

/**
 * Thrown when no applicable price is found for the given query criteria.
 */
public class PriceNotFoundException extends RuntimeException {

    public PriceNotFoundException(Long brandId, Long productId, LocalDateTime applicationDate) {
        super(String.format("No applicable price found for brandId=%d, productId=%d at %s",
                brandId, productId, applicationDate));
    }
}
