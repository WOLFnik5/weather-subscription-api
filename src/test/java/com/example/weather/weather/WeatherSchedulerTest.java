package com.example.weather.weather;

import com.example.weather.notification.NotificationService;
import com.example.weather.subscription.Subscription;
import com.example.weather.subscription.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherSchedulerTest {

    @Mock
    private SubscriptionRepository repository;
    @Mock
    private WeatherClient weatherClient;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private WeatherScheduler scheduler;

    @Test
    void sendUpdatesUsesNotificationService() {
        Subscription sub = Subscription.builder()
                .email("test@example.com")
                .city("Kyiv")
                .build();
        when(repository.findAll()).thenReturn(List.of(sub));
        when(weatherClient.fetchCurrentTemperature("Kyiv")).thenReturn("23");

        scheduler.sendUpdates();

        verify(notificationService).send("test@example.com", "Weather in Kyiv is 23°C");
    }
}
