package com.example.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error details")
public class ErrorResponse {
    @Schema(description = "Human-readable error message")
    private String message;
    @Schema(description = "Application-specific error code")
    private String errorCode;
}

