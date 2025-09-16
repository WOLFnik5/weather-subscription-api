package com.example.weather.mapper;

import com.example.weather.dto.SubscriptionDto;
import com.example.weather.dto.request.SubscriptionRequest;
import com.example.weather.entity.Subscription;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionMapperTest {

    private final SubscriptionMapper mapper = Mappers.getMapper(SubscriptionMapper.class);

    @Test
    void shouldMapRequestToEntityAndLowercaseEmail() {
        // given
        SubscriptionRequest request = SubscriptionRequest.builder()
                .email("TEST@Example.COM")
                .city("Kyiv")
                .build();

        // when
        Subscription entity = mapper.toEntity(request);

        // then
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getCity()).isEqualTo("Kyiv");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void shouldMapEntityToDto() {
        // given
        Subscription entity = Subscription.builder()
                .id(42L)
                .email("user@example.com")
                .city("Lviv")
                .build();

        // when
        SubscriptionDto dto = mapper.toDto(entity);

        // then
        assertThat(dto.getId()).isEqualTo(42L);
        assertThat(dto.getEmail()).isEqualTo("user@example.com");
        assertThat(dto.getCity()).isEqualTo("Lviv");
    }
}
