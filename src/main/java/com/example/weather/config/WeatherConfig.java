package com.example.weather.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for weather-related beans.
 */
@Configuration
public class WeatherConfig {

    /**
     * RestTemplateBuilder configured with connect and read timeouts.
     *
     * @param builder default RestTemplate builder
     * @return configured RestTemplateBuilder
     */
    @Bean
    public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5));
    }
}

