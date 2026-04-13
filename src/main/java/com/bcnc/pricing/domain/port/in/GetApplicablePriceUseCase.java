package com.bcnc.pricing.domain.port.in;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceQuery;

/**
 * Input port for retrieving the applicable price.
 * Returns the highest-priority price whose validity period covers the requested date.
 *
 * @throws com.bcnc.pricing.domain.model.PriceNotFoundException if no price matches the query
 */
public interface GetApplicablePriceUseCase {

    Price findApplicablePrice(PriceQuery query);
}
