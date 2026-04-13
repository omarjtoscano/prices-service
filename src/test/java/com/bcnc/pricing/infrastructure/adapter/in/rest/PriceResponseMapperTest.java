package com.bcnc.pricing.infrastructure.adapter.in.rest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bcnc.pricing.domain.model.Price;

class PriceResponseMapperTest {

    @Test
    @DisplayName("Should map all fields from Price domain to PriceResponse")
    void shouldMapAllFields_fromDomainToResponse() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

        Price price = Price.builder()
                .productId(35455L)
                .brandId(1L)
                .priceList(2)
                .startDate(start)
                .endDate(end)
                .priority(1)
                .amount(new BigDecimal("25.45"))
                .currency("EUR")
                .build();

        PriceResponse result = PriceResponseMapper.toResponse(price);

        assertThat(result.getProductId()).isEqualTo(35455L);
        assertThat(result.getBrandId()).isEqualTo(1L);
        assertThat(result.getPriceList()).isEqualTo(2);
        assertThat(result.getStartDate()).isEqualTo(start);
        assertThat(result.getEndDate()).isEqualTo(end);
        assertThat(result.getPrice()).isEqualByComparingTo("25.45");
        assertThat(result.getCurr()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should correctly map renamed fields: domain.amount -> response.price, domain.currency -> response.curr")
    void shouldMapRenamedFields_correctly() {
        Price price = Price.builder()
                .productId(1L)
                .brandId(1L)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 1, 1, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(0)
                .amount(new BigDecimal("99.99"))
                .currency("USD")
                .build();

        PriceResponse result = PriceResponseMapper.toResponse(price);

        assertThat(result.getPrice()).isEqualByComparingTo("99.99");
        assertThat(result.getCurr()).isEqualTo("USD");
    }
}
