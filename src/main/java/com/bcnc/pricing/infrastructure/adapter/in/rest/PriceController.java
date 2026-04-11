package com.bcnc.pricing.infrastructure.adapter.in.rest;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcnc.pricing.domain.model.Price;
import com.bcnc.pricing.domain.model.PriceNotFoundException;
import com.bcnc.pricing.domain.model.PriceQuery;
import com.bcnc.pricing.domain.port.in.GetApplicablePriceUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/prices")
@Validated
@Tag(name = "Prices", description = "Price query operations")
public class PriceController {

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;

    public PriceController(GetApplicablePriceUseCase getApplicablePriceUseCase) {
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Get applicable price",
            description = "Returns the applicable price for a given product, brand, and application date based on priority resolution"
    )
    @ApiResponse(responseCode = "200", description = "Price found",
            content = @Content(schema = @Schema(implementation = PriceResponse.class)))
    @ApiResponse(responseCode = "404", description = "No applicable price found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid or missing parameters",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<PriceResponse> getApplicablePrice(
            @Parameter(description = "Application date in ISO format (yyyy-MM-dd'T'HH:mm:ss)", example = "2020-06-14T10:00:00")
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @Parameter(description = "Product identifier", example = "35455")
            @RequestParam @Positive Long productId,
            @Parameter(description = "Brand identifier", example = "1")
            @RequestParam @Positive Long brandId) {

        log.info("Incoming price request: productId={}, brandId={}, applicationDate={}", productId, brandId, applicationDate);

        PriceQuery query = new PriceQuery(applicationDate, productId, brandId);

        Price price = getApplicablePriceUseCase.findApplicablePrice(query)
                .orElseThrow(() -> new PriceNotFoundException(brandId, productId));

        log.info("Price resolved: productId={}, brandId={}, priceList={}", productId, brandId, price.getPriceList());

        return ResponseEntity.ok(PriceResponseMapper.toResponse(price));
    }
}
