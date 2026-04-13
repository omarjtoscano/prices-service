package com.bcnc.pricing.application.service;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceNotFoundException;
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
    public Price findApplicablePrice(PriceQuery query) {
        log.debug("Executing use case findApplicablePrice: productId={}, brandId={}, applicationDate={}",
                query.getProductId(), query.getBrandId(), query.getApplicationDate());

        return priceRepositoryPort.findApplicablePrice(query)
                .orElseThrow(() -> new PriceNotFoundException(
                        query.getBrandId(), query.getProductId(), query.getApplicationDate()));
    }
}
