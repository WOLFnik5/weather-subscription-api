package com.example.weather.controller;

import com.example.weather.exception.BadRequestException;
import com.example.weather.entity.Subscription;
import com.example.weather.dto.SubscriptionDto;
import com.example.weather.dto.request.SubscriptionRequest;
import com.example.weather.service.SubscriptionService;
import com.example.weather.mapper.SubscriptionMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions")
@SuppressFBWarnings(value = "EI_EXPOSE_REP2",
        justification = "Spring-managed services are treated as immutable dependencies")
public class SubscriptionController {

    private final SubscriptionService service;
    private final SubscriptionMapper mapper;


    @PostMapping
    @Operation(summary = "Create subscription")
    public ResponseEntity<SubscriptionDto> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        Subscription subscription = service.create(request);
        return new ResponseEntity<>(mapper.toDto(subscription), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List subscriptions")
    public Page<SubscriptionDto> list(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        if (page < 0) {
            throw new BadRequestException("Page index must not be less than zero");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("Page size must be between 1 and 100");
        }
        var pageable = PageRequest.of(page, size);
        return service.findAll(pageable)
                .map(mapper::toDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete subscription")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
