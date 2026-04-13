package com.bcnc.pricing.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceQuery;

@ExtendWith(MockitoExtension.class)
class PricePersistenceAdapterTest {

    @Mock
    private PriceJpaRepository jpaRepository;

    @InjectMocks
    private PricePersistenceAdapter adapter;

    private static final Long BRAND_ID = 1L;
    private static final Long PRODUCT_ID = 35455L;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0);
    private static final PriceQuery QUERY = new PriceQuery(APPLICATION_DATE, PRODUCT_ID, BRAND_ID);

    @Test
    @DisplayName("Should delegate to JPA repository and map entity to domain model")
    void shouldDelegateToRepository_andMapResult() {
        PriceEntity entity = new PriceEntity(
                1L, BRAND_ID,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                1, PRODUCT_ID, 0, new BigDecimal("35.50"), "EUR");

        when(jpaRepository.findTopByBrandAndProductAndDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(Optional.of(entity));

        Optional<Price> result = adapter.findApplicablePrice(QUERY);

        assertThat(result).isPresent();
        assertThat(result.get().getBrandId()).isEqualTo(BRAND_ID);
        assertThat(result.get().getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(result.get().getPriceList()).isEqualTo(1);
        assertThat(result.get().getAmount()).isEqualByComparingTo("35.50");
        assertThat(result.get().getCurrency()).isEqualTo("EUR");
        verify(jpaRepository).findTopByBrandAndProductAndDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("Should return empty when repository finds no matching price")
    void shouldReturnEmpty_whenRepositoryFindsNothing() {
        when(jpaRepository.findTopByBrandAndProductAndDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE))
                .thenReturn(Optional.empty());

        Optional<Price> result = adapter.findApplicablePrice(QUERY);

        assertThat(result).isEmpty();
        verify(jpaRepository).findTopByBrandAndProductAndDate(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);
    }
}
