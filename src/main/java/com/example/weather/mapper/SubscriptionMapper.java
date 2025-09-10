package com.example.weather.mapper;

import com.example.weather.model.Subscription;
import com.example.weather.model.SubscriptionDto;
import com.example.weather.model.SubscriptionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionDto toDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", expression = "java(request.getEmail().toLowerCase())")
    Subscription toEntity(SubscriptionRequest request);
}

