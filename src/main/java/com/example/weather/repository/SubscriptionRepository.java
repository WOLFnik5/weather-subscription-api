package com.example.weather.repository;

import com.example.weather.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByEmailAndCity(String email, String city);
}
