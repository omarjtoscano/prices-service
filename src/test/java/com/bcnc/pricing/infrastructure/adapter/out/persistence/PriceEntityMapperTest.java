package com.bcnc.pricing.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bcnc.pricing.domain.model.Price;

class PriceEntityMapperTest {

    @Test
    @DisplayName("Should map all fields from PriceEntity to Price domain model")
    void shouldMapAllFields_fromEntityToDomain() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

        PriceEntity entity = new PriceEntity(
                1L, 1L, start, end, 2, 35455L, 1, new BigDecimal("25.45"), "EUR");

        Price result = PriceEntityMapper.toDomain(entity);

        assertThat(result.getBrandId()).isEqualTo(1L);
        assertThat(result.getProductId()).isEqualTo(35455L);
        assertThat(result.getPriceList()).isEqualTo(2);
        assertThat(result.getStartDate()).isEqualTo(start);
        assertThat(result.getEndDate()).isEqualTo(end);
        assertThat(result.getPriority()).isEqualTo(1);
        assertThat(result.getAmount()).isEqualByComparingTo("25.45");
        assertThat(result.getCurrency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should correctly map renamed fields: entity.price -> domain.amount, entity.curr -> domain.currency")
    void shouldMapRenamedFields_correctly() {
        PriceEntity entity = new PriceEntity(
                1L, 1L,
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                1, 35455L, 0, new BigDecimal("99.99"), "USD");

        Price result = PriceEntityMapper.toDomain(entity);

        assertThat(result.getAmount()).isEqualByComparingTo("99.99");
        assertThat(result.getCurrency()).isEqualTo("USD");
    }
}
