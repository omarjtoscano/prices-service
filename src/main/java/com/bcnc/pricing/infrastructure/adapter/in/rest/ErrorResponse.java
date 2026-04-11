package com.bcnc.pricing.infrastructure.adapter.in.rest;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
}
