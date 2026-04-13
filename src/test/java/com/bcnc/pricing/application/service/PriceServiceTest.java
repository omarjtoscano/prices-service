package com.bcnc.pricing.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceNotFoundException;
import com.bcnc.pricing.domain.model.PriceQuery;
import com.bcnc.pricing.domain.port.out.PriceRepositoryPort;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepositoryPort priceRepositoryPort;

    @InjectMocks
    private PriceService priceService;

    private PriceQuery query;
    private Price expectedPrice;

    @BeforeEach
    void setUp() {
        query = new PriceQuery(
                LocalDateTime.of(2020, 6, 14, 10, 0),
                35455L,
                1L);

        expectedPrice = Price.builder()
                .brandId(1L)
                .productId(35455L)
                .priceList(1)
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59, 59))
                .priority(0)
                .amount(new BigDecimal("35.50"))
                .currency("EUR")
                .build();
    }

    @Test
    @DisplayName("Should return price when repository finds an applicable price")
    void shouldReturnPrice_whenRepositoryFindsApplicablePrice() {
        when(priceRepositoryPort.findApplicablePrice(query))
                .thenReturn(Optional.of(expectedPrice));

        Price result = priceService.findApplicablePrice(query);

        assertThat(result.getPriceList()).isEqualTo(1);
        assertThat(result.getAmount()).isEqualByComparingTo("35.50");
        verify(priceRepositoryPort).findApplicablePrice(query);
    }

    @Test
    @DisplayName("Should throw PriceNotFoundException when no applicable price exists")
    void shouldThrowPriceNotFoundException_whenNoApplicablePriceExists() {
        when(priceRepositoryPort.findApplicablePrice(query))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> priceService.findApplicablePrice(query))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("brandId=1")
                .hasMessageContaining("productId=35455");

        verify(priceRepositoryPort).findApplicablePrice(query);
    }
}
