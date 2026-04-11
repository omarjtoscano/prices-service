package com.bcnc.pricing.domain.port.in;

import java.util.Optional;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceQuery;

public interface GetApplicablePriceUseCase {

    Optional<Price> findApplicablePrice (PriceQuery query);
}
