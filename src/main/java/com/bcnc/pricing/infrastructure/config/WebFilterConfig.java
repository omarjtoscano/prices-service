package com.bcnc.pricing.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bcnc.pricing.infrastructure.adapter.in.rest.MdcRequestFilter;

@Configuration
public class WebFilterConfig {

    @Bean
    MdcRequestFilter mdcRequestFilter() {
        return new MdcRequestFilter();
    }
}
