package com.example.weather.weather;

import com.example.weather.model.Subscription;
import com.example.weather.repository.SubscriptionRepository;
import com.example.weather.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by("id"));
        Page<Subscription> page;
        Map<String, List<Subscription>> grouped = new HashMap<>();

        do {
            page = repository.findAll(pageable);
            page.forEach(sub ->
                    grouped.computeIfAbsent(sub.getCity(), c -> new ArrayList<>()).add(sub));
            pageable = page.nextPageable();
        } while (page.hasNext());

        grouped.forEach((city, subs) -> {
            String temp = weatherClient.fetchCurrentTemperature(city);
            String message = temp.equals("n/a")
                    ? String.format("Weather in %s is unavailable", city)
                    : String.format("Weather in %s is %s°C", city, temp);
            subs.forEach(sub -> {
                notificationService.send(sub.getEmail(), message);
                log.info("Notified {} about {}", sub.getEmail(), city);
            });
        });
    }
}
