package com.bcnc.pricing.domain.model;

public class PriceNotFoundException extends RuntimeException {

    public PriceNotFoundException(Long brandId, Long productId) {
        super(String.format("No applicable price found for brandId=%d and productId=%d", brandId, productId));
    }
}
