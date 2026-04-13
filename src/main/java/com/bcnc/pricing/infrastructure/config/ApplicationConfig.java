package com.bcnc.pricing.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bcnc.pricing.application.service.PriceService;
import com.bcnc.pricing.domain.port.in.GetApplicablePriceUseCase;
import com.bcnc.pricing.domain.port.out.PriceRepositoryPort;
import com.bcnc.pricing.infrastructure.adapter.out.persistence.PriceJpaRepository;
import com.bcnc.pricing.infrastructure.adapter.out.persistence.PricePersistenceAdapter;

@Configuration
public class ApplicationConfig {

    @Bean
    PriceRepositoryPort priceRepositoryPort(PriceJpaRepository jpaRepository) {
        return new PricePersistenceAdapter(jpaRepository);
    }

    @Bean
    GetApplicablePriceUseCase getApplicablePriceUseCase(PriceRepositoryPort priceRepositoryPort) {
        return new PriceService(priceRepositoryPort);
    }
}
