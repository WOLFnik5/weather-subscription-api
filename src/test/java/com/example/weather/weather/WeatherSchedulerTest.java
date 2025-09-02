package com.example.weather.weather;

import com.example.weather.model.Subscription;
import com.example.weather.repository.SubscriptionRepository;
import com.example.weather.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        when(notificationService.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

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
        assertThat(pageable.getSort()).isEqualTo(Sort.by("id"));
    }

    @Test
    void fetchWeatherCalledOncePerCity() {
        Subscription kyiv1 = Subscription.builder()
                .email("a@example.com")
                .city("Kyiv")
                .build();
        Subscription kyiv2 = Subscription.builder()
                .email("b@example.com")
                .city("Kyiv")
                .build();
        Subscription lviv = Subscription.builder()
                .email("c@example.com")
                .city("Lviv")
                .build();

        when(repository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(kyiv1, kyiv2, lviv)));
        when(weatherClient.fetchCurrentTemperature("Kyiv")).thenReturn("20");
        when(weatherClient.fetchCurrentTemperature("Lviv")).thenReturn("15");
        when(notificationService.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        scheduler.sendUpdates();

        verify(weatherClient, times(1)).fetchCurrentTemperature("Kyiv");
        verify(weatherClient, times(1)).fetchCurrentTemperature("Lviv");
        verify(notificationService).send("a@example.com", "Weather in Kyiv is 20°C");
        verify(notificationService).send("b@example.com", "Weather in Kyiv is 20°C");
        verify(notificationService).send("c@example.com", "Weather in Lviv is 15°C");
    }

    @Test
    void sendUpdatesThrowsWhenNotificationFails() {
        Subscription ok = Subscription.builder()
                .email("a@example.com")
                .city("Kyiv")
                .build();
        Subscription fail = Subscription.builder()
                .email("b@example.com")
                .city("Kyiv")
                .build();

        when(repository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ok, fail)));
        when(weatherClient.fetchCurrentTemperature("Kyiv")).thenReturn("25");

        when(notificationService.send(eq("a@example.com"), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(notificationService.send(eq("b@example.com"), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("boom")));

        assertThatThrownBy(() -> scheduler.sendUpdates())
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
