package com.example.weather.weather;

import com.example.weather.model.Subscription;
import com.example.weather.repository.SubscriptionRepository;
import com.example.weather.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @ParameterizedTest
    @CsvSource({
            "23,Weather in Kyiv is 23°C",
            "n/a,Weather in Kyiv is unavailable"
    })
    void sendUpdatesUsesNotificationService(String temp, String expectedMessage) {
        Subscription sub = Subscription.builder()
                .email("test@example.com")
                .city("Kyiv")
                .build();
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(sub)));
        when(weatherClient.fetchCurrentTemperature("Kyiv")).thenReturn(temp);

        scheduler.sendUpdates();

        verify(notificationService).send("test@example.com", expectedMessage);
    }

    @Test
    void findAllCalledWithExpectedPageable() {
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        scheduler.sendUpdates();

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(captor.capture());
        Pageable pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(20);
    }
}
