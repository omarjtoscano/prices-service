package com.bcnc.pricing.domain.port.out;

import java.util.Optional;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceQuery;

/**
 * Output port for price persistence queries.
 * Finds the highest-priority price matching the brand, product, and date criteria
 * encapsulated in a {@link PriceQuery}.
 */
public interface PriceRepositoryPort {

    Optional<Price> findApplicablePrice(PriceQuery query);
}
