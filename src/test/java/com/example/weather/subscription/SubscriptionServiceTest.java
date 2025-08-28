package com.example.weather.subscription;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SubscriptionServiceTest {

    @Autowired
    private SubscriptionService service;

    @Autowired
    private SubscriptionRepository repository;

    @Test
    void creatingDuplicateSubscriptionIsNotAllowed() {
        repository.deleteAll();
        SubscriptionRequest request = new SubscriptionRequest("test@example.com", "Kyiv");
        service.create(request);
        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(repository.count()).isEqualTo(1);
    }
}
