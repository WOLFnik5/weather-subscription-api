package com.example.weather.service;

import com.example.weather.model.Subscription;
import com.example.weather.model.SubscriptionDto;
import com.example.weather.model.SubscriptionRequest;
import com.example.weather.repository.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String email = request.getEmail().toLowerCase();

        repository.findByEmailAndCity(email, request.getCity())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Subscription already exists for this email and city");
                });

        Subscription subscription = Subscription.builder()
                .email(email)
                .city(request.getCity())
                .build();
        try {
            return repository.save(subscription);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Subscription already exists for this email and city");
        }
    }

    @Transactional(readOnly = true)
    public Page<Subscription> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public void delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(repository::delete,
                        () -> {
                            throw new EntityNotFoundException("Subscription not found with id " + id);
                        });
    }
}
