package com.bcnc.pricing.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


@DataJpaTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PriceJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PriceJpaRepository repository;

    private static final Long   BRAND_ID   = 1L;
    private static final Long   PRODUCT_ID = 35455L;


    private PriceEntity persistPrice(int priceList,
                                     LocalDateTime start,
                                     LocalDateTime end,
                                     int priority,
                                     String amount) {
        PriceEntity entity = new PriceEntity(
                null, BRAND_ID, start, end, priceList, PRODUCT_ID,
                priority, new BigDecimal(amount), "EUR");
        return entityManager.persistAndFlush(entity);
    }

    @Test
    @DisplayName("Returns empty when no price covers the requested date")
    void shouldReturnEmpty_whenDateOutsideAllRanges() {
        LocalDateTime rangeEnd = LocalDateTime.of(2020, 6, 13, 23, 59, 59);
        persistPrice(1, LocalDateTime.of(2020, 1, 1, 0, 0), rangeEnd, 0, "35.50");

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(
                BRAND_ID, PRODUCT_ID, LocalDateTime.of(2020, 6, 14, 0, 0));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns the price with highest priority when multiple prices cover the same date")
    void shouldReturnHighestPriority_whenMultiplePricesApply() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

        persistPrice(1, start, end, 0, "35.50");
        persistPrice(2, start, end, 1, "25.45");

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(
                BRAND_ID, PRODUCT_ID, LocalDateTime.of(2020, 6, 14, 16, 0));

        assertThat(result).isPresent();
        assertThat(result.get().getPriority()).isEqualTo(1);
        assertThat(result.get().getPriceList()).isEqualTo(2);
        assertThat(result.get().getPrice()).isEqualByComparingTo("25.45");
    }

    @Test
    @DisplayName("Returns only one result even when three prices apply, picking the highest priority")
    void shouldReturnHighestPriorityOne_whenThreePricesApply() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

        persistPrice(1, start, end, 0, "35.50");
        persistPrice(2, start, end, 1, "25.45");
        persistPrice(3, start, end, 2, "20.00");

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(
                BRAND_ID, PRODUCT_ID, LocalDateTime.of(2020, 6, 14, 12, 0));

        assertThat(result).isPresent();
        assertThat(result.get().getPriority()).isEqualTo(2);
        assertThat(result.get().getPriceList()).isEqualTo(3);
        assertThat(result.get().getPrice()).isEqualByComparingTo("20.00");
    }

    @Test
    @DisplayName("Matches price when applicationDate equals startDate exactly (inclusive lower bound)")
    void shouldMatchPrice_whenDateIsExactlyAtStartBoundary() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 15, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 6, 15, 11, 0);
        persistPrice(3, start, end, 1, "30.50");

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(BRAND_ID, PRODUCT_ID, start);

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(3);
    }

    @Test
    @DisplayName("Matches price when applicationDate equals endDate exactly (inclusive upper bound)")
    void shouldMatchPrice_whenDateIsExactlyAtEndBoundary() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 15, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 6, 15, 11, 0);
        persistPrice(3, start, end, 1, "30.50");

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(BRAND_ID, PRODUCT_ID, end);

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(3);
    }

    @Test
    @DisplayName("Returns empty when applicationDate is one second before startDate")
    void shouldReturnEmpty_whenDateIsOneSecondBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 15, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 6, 15, 11, 0);
        persistPrice(3, start, end, 1, "30.50");

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(
                BRAND_ID, PRODUCT_ID, start.minusSeconds(1));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns empty when applicationDate is one second after endDate")
    void shouldReturnEmpty_whenDateIsOneSecondAfterEnd() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 15, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 6, 15, 11, 0);
        persistPrice(3, start, end, 1, "30.50");

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(
                BRAND_ID, PRODUCT_ID, end.plusSeconds(1));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns empty when only a different brandId has a matching price")
    void shouldReturnEmpty_whenBrandIdDoesNotMatch() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
        entityManager.persistAndFlush(
                new PriceEntity(null, 2L, start, end, 1, PRODUCT_ID, 0, new BigDecimal("35.50"), "EUR"));

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(
                BRAND_ID, PRODUCT_ID, LocalDateTime.of(2020, 6, 14, 10, 0));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns empty when only a different productId has a matching price")
    void shouldReturnEmpty_whenProductIdDoesNotMatch() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
        entityManager.persistAndFlush(
                new PriceEntity(null, BRAND_ID, start, end, 1, 99999L, 0, new BigDecimal("35.50"), "EUR"));

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(
                BRAND_ID, PRODUCT_ID, LocalDateTime.of(2020, 6, 14, 10, 0));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns exactly one result (not an exception) when two prices share the same priority")
    void shouldReturnExactlyOne_whenTwoPricesHaveEqualPriority() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

        persistPrice(1, start, end, 1, "35.50");
        persistPrice(2, start, end, 1, "25.45");

        Optional<PriceEntity> result = repository.findTopByBrandAndProductAndDate(
                BRAND_ID, PRODUCT_ID, LocalDateTime.of(2020, 6, 14, 12, 0));

        assertThat(result).isPresent();
        assertThat(result.get().getPriority()).isEqualTo(1);
        assertThat(result.get().getPriceList()).isIn(1, 2);
        assertThat(result.get().getPrice()).isIn(new BigDecimal("35.50"), new BigDecimal("25.45"));
    }
}
