package com.bcnc.pricing.domain.model;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PriceNotFoundExceptionTest {

    @Test
    @DisplayName("Should build message with brandId, productId and applicationDate")
    void shouldBuildMessage_withBrandIdProductIdAndApplicationDate() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        PriceNotFoundException exception = new PriceNotFoundException(1L, 35455L, applicationDate);

        assertThat(exception.getMessage())
                .contains("brandId=1")
                .contains("productId=35455")
                .contains("2020-06-14T10:00");
    }
}
