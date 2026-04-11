package com.bcnc.pricing.infrastructure.adapter.out.persistence;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.port.out.PriceRepositoryPort;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class PricePersistenceAdapter implements PriceRepositoryPort {

    private final PriceJpaRepository jpaRepository;

    public PricePersistenceAdapter(PriceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Price> findApplicablePrice(Long brandId, Long productId, LocalDateTime applicationDate) {
        log.debug("Querying price: brandId={}, productId={}, applicationDate={}", brandId, productId, applicationDate);

        Optional<Price> result = jpaRepository.findTopByBrandAndProductAndDate(brandId, productId, applicationDate)
                .map(PriceEntityMapper::toDomain);

        log.debug("Query result: brandId={}, productId={} -> {}", brandId, productId,
                result.isPresent() ? "found (priceList=" + result.get().getPriceList() + ")" : "not found");

        return result;
    }
}
