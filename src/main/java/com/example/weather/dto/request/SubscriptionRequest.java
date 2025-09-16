package com.example.weather.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Subscription request")
public class SubscriptionRequest {
    @Email
    @NotBlank
    @Schema(description = "Subscriber email")
    private String email;

    @NotBlank
    @Schema(description = "City for weather updates")
    private String city;
}
