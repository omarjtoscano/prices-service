package com.bcnc.pricing.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pricesServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Prices Service API")
                        .description("REST API for querying applicable prices with priority resolution")
                        .version("1.0.0"));
    }
}
