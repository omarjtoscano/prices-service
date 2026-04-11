package com.bcnc.pricing.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PriceNotFoundExceptionTest {

    @Test
    @DisplayName("Should build message with brandId and productId")
    void shouldBuildMessage_withBrandIdAndProductId() {
        PriceNotFoundException exception = new PriceNotFoundException(1L, 35455L);

        assertThat(exception.getMessage())
                .contains("brandId=1")
                .contains("productId=35455");
    }
}
