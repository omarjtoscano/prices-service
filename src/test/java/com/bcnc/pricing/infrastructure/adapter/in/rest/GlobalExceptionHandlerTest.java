package com.bcnc.pricing.infrastructure.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 500 with generic message for unexpected exceptions")
    void shouldReturn500_whenUnexpectedExceptionOccurs() {
        Exception unexpected = new RuntimeException("something went wrong");

        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(unexpected);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("Internal server error");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
