package com.bcnc.pricing.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final int statusCode;
    private final String message;
    private final LocalDateTime timestamp;
}
