package com.example.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Subscription data")
public class SubscriptionDto {
    @Schema(description = "Unique identifier")
    private Long id;
    @Schema(description = "Subscriber email address")
    private String email;
    @Schema(description = "City for weather updates")
    private String city;
}
