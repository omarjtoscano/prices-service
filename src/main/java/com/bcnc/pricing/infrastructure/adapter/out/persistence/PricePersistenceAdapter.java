package com.bcnc.pricing.infrastructure.adapter.out.persistence;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceQuery;
import com.bcnc.pricing.domain.port.out.PriceRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class PricePersistenceAdapter implements PriceRepositoryPort {

    private final PriceJpaRepository jpaRepository;

    public PricePersistenceAdapter(PriceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Price> findApplicablePrice(PriceQuery query) {
        log.debug("Querying price: brandId={}, productId={}, applicationDate={}",
                query.getBrandId(), query.getProductId(), query.getApplicationDate());

        Optional<Price> result = jpaRepository.findTopByBrandAndProductAndDate(
                        query.getBrandId(), query.getProductId(), query.getApplicationDate())
                .map(PriceEntityMapper::toDomain);

        log.debug("Query result: brandId={}, productId={} -> {}", query.getBrandId(), query.getProductId(),
                result.isPresent() ? "found (priceList=" + result.get().getPriceList() + ")" : "not found");

        return result;
    }
}
