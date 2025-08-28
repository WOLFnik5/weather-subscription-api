package com.example.weather.subscription;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;

    public Subscription create(SubscriptionRequest request) {
        Subscription subscription = Subscription.builder()
                .email(request.email())
                .city(request.city())
                .build();
        return repository.save(subscription);
    }

    public List<Subscription> findAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Subscription not found with id " + id);
        }
        repository.deleteById(id);
    }
}
