package com.bcnc.pricing.application.service;

import java.util.Optional;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceQuery;
import com.bcnc.pricing.domain.port.in.GetApplicablePriceUseCase;
import com.bcnc.pricing.domain.port.out.PriceRepositoryPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PriceService implements GetApplicablePriceUseCase {

    private final PriceRepositoryPort priceRepositoryPort;

    public PriceService(PriceRepositoryPort priceRepositoryPort) {
        this.priceRepositoryPort = priceRepositoryPort;
    }

    @Override
    public Optional<Price> findApplicablePrice(PriceQuery query) {
        log.info("Executing use case findApplicablePrice: productId={}, brandId={}, applicationDate={}",
                query.getProductId(), query.getBrandId(), query.getApplicationDate());

        Optional<Price> result = priceRepositoryPort.findApplicablePrice(
                query.getBrandId(),
                query.getProductId(),
                query.getApplicationDate());

        if (result.isEmpty()) {
            log.warn("No applicable price found: productId={}, brandId={}, applicationDate={}",
                    query.getProductId(), query.getBrandId(), query.getApplicationDate());
        } else {
            log.info("Price found for productId={}, brandId={}, priceList={}",
                    query.getProductId(), query.getBrandId(), result.get().getPriceList());
        }

        return result;
    }
}
