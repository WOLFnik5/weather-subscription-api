package com.example.weather.weather;

import com.example.weather.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherScheduler.class);
    private final SubscriptionRepository repository;
    private final WeatherClient weatherClient;

    @Scheduled(fixedRateString = "${weather.update-interval-ms:3600000}")
    public void sendUpdates() {
        repository.findAll().forEach(sub -> {
            String temp = weatherClient.fetchCurrentTemperature(sub.getCity());
            log.info("Weather in {} is {}°C. Notifying {}", sub.getCity(), temp, sub.getEmail());
        });
    }
}
