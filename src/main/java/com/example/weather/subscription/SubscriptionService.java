package com.example.weather.subscription;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;

    @Transactional
    public Subscription create(SubscriptionRequest request) {
        repository.findByEmailAndCity(request.email(), request.city())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Subscription already exists for this email and city");
                });

        Subscription subscription = Subscription.builder()
                .email(request.email())
                .city(request.city())
                .build();
        return repository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<Subscription> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Subscription not found with id " + id);
        }
        repository.deleteById(id);
    }
}
