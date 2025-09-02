package com.example.weather.weather;

import com.example.weather.repository.SubscriptionRepository;
import com.example.weather.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherScheduler.class);
    private static final int PAGE_SIZE = 20;

    private final SubscriptionRepository repository;
    private final WeatherClient weatherClient;
    private final NotificationService notificationService;

    @Scheduled(fixedRateString = "${weather.update-interval-ms:3600000}")
    public void sendUpdates() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        Page<com.example.weather.model.Subscription> page;

        do {
            page = repository.findAll(pageable);
            page.forEach(sub -> {
                String temp = weatherClient.fetchCurrentTemperature(sub.getCity());
                String message;
                if (temp.equals("n/a")) {
                    message = String.format("Weather in %s is unavailable", sub.getCity());
                } else {
                    message = String.format("Weather in %s is %s°C", sub.getCity(), temp);
                }
                notificationService.send(sub.getEmail(), message);
                log.info("Notified {} about {}", sub.getEmail(), sub.getCity());
            });
            pageable = page.nextPageable();
        } while (page.hasNext());
    }
}
