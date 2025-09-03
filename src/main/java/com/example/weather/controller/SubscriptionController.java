package com.example.weather.controller;

import com.example.weather.model.Subscription;
import com.example.weather.model.SubscriptionDto;
import com.example.weather.model.SubscriptionRequest;
import com.example.weather.service.SubscriptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {

    private final SubscriptionService service;


    @PostMapping
    public ResponseEntity<SubscriptionDto> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        Subscription subscription = service.create(request);
        return new ResponseEntity<>(service.toDto(subscription), HttpStatus.CREATED);
    }

    @GetMapping
    public Page<SubscriptionDto> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        var pageable = PageRequest.of(page, size);
        return service.findAll(pageable)
                .map(service::toDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
