package com.example.weather.mapper;

import com.example.weather.entity.Subscription;
import com.example.weather.dto.SubscriptionDto;
import com.example.weather.dto.request.SubscriptionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Locale;

@Mapper(componentModel = "spring", imports = {Locale.class})
public interface SubscriptionMapper {

    SubscriptionDto toDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", expression = "java(request.getEmail().toLowerCase(Locale.ROOT))")
    Subscription toEntity(SubscriptionRequest request);
}

