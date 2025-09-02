package com.example.weather.service;

import com.example.weather.model.Subscription;
import com.example.weather.model.SubscriptionDto;
import com.example.weather.model.SubscriptionRequest;
import com.example.weather.repository.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;

    public SubscriptionDto toDto(Subscription subscription) {
        return new SubscriptionDto(
                subscription.getId(),
                subscription.getEmail(),
                subscription.getCity()
        );
    }

    @Transactional
    public Subscription create(SubscriptionRequest request) {
        repository.findByEmailAndCity(request.getEmail(), request.getCity())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Subscription already exists for this email and city");
                });

        Subscription subscription = Subscription.builder()
                .email(request.getEmail())
                .city(request.getCity())
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
