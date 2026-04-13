package com.bcnc.pricing.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bcnc.pricing.domain.model.PriceNotFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFound(PriceNotFoundException ex) {
        log.warn("Price not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("Missing required parameter: '{}'", ex.getParameterName());
        String message = String.format("Missing required parameter: '%s'", ex.getParameterName());
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch for parameter '{}': received '{}'", ex.getName(), ex.getValue());
        String message = String.format("Invalid value for parameter '%s': '%s'", ex.getName(), ex.getValue());
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath().toString();
                    String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                    return String.format("Invalid value for parameter '%s': %s", field, v.getMessage());
                })
                .collect(Collectors.joining("; "));
        log.warn("Constraint violation: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = ErrorResponse.builder()
                .statusCode(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
