package com.example.weather.weather;

import com.example.weather.repository.SubscriptionRepository;
import com.example.weather.service.NotificationService;
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
    private final NotificationService notificationService;

    @Scheduled(fixedRateString = "${weather.update-interval-ms:3600000}")
    public void sendUpdates() {
        repository.findAll().forEach(sub -> {
            String temp = weatherClient.fetchCurrentTemperature(sub.getCity());
            String message = String.format("Weather in %s is %s°C", sub.getCity(), temp);
            notificationService.send(sub.getEmail(), message);
            log.info("Notified {} about {}", sub.getEmail(), sub.getCity());
        });
    }
}
