package com.bcnc.pricing.domain.port.out;

import com.bcnc.pricing.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepositoryPort {

    Optional<Price> findApplicablePrice(Long brandId, Long productId, LocalDateTime applicationDate);
}
