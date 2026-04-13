package com.bcnc.pricing.infrastructure.adapter.in.rest;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

class GlobalExceptionHandlerTest {

    private static final Logger HANDLER_LOGGER =
            (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static Level originalLevel;

    @BeforeAll
    static void suppressErrorLog() {
        originalLevel = HANDLER_LOGGER.getLevel();
        HANDLER_LOGGER.setLevel(Level.OFF);
    }

    @AfterAll
    static void restoreLogLevel() {
        HANDLER_LOGGER.setLevel(originalLevel);
    }

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 500 with generic message for unexpected exceptions")
    void shouldReturn500_whenUnexpectedExceptionOccurs() {
        Exception unexpected = new RuntimeException("something went wrong");

        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(unexpected);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();

        ErrorResponse body = response.getBody();
        assertThat(body.getStatusCode()).isEqualTo(500);
        assertThat(body.getMessage()).isEqualTo("Internal server error");
        assertThat(body.getTimestamp()).isNotNull();
    }
}
