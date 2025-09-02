package com.example.weather.service;

import com.example.weather.model.SubscriptionRequest;
import com.example.weather.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
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

    @Test
    void deletingNonExistentSubscriptionThrowsException() {
        repository.deleteAll();
        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deletingExistingSubscriptionRemovesIt() {
        repository.deleteAll();
        SubscriptionRequest request = new SubscriptionRequest("test@example.com", "Kyiv");
        var subscription = service.create(request);
        service.delete(subscription.getId());
        assertThat(repository.existsById(subscription.getId())).isFalse();
    }
}
