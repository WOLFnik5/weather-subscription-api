package com.example.weather.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class WeatherClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private WeatherClient client;

    @BeforeEach
    void setUp() {
        when(builder.build()).thenReturn(restTemplate);
        client = new WeatherClient(builder);
    }

    @Test
    void returnsNaWhenResponseIsNull() {
        when(restTemplate.getForObject(any(URI.class), eq(JsonNode.class))).thenReturn(null);

        String temp = client.fetchCurrentTemperature("Kyiv");

        assertThat(temp).isEqualTo("n/a");
    }

    @Test
    void extractsTemperatureFromValidJson() throws Exception {
        String json = "{ \"current_condition\": [{\"temp_C\": \"25\"}] }";
        JsonNode node = new ObjectMapper().readTree(json);
        when(restTemplate.getForObject(any(URI.class), eq(JsonNode.class))).thenReturn(node);

        String temp = client.fetchCurrentTemperature("Kyiv");

        assertThat(temp).isEqualTo("25");
    }

    @Test
    void logsAndReturnsNaOnException(CapturedOutput output) {
        when(restTemplate.getForObject(any(URI.class), eq(JsonNode.class)))
                .thenThrow(new RuntimeException("boom"));

        String temp = client.fetchCurrentTemperature("Kyiv");

        assertThat(temp).isEqualTo("n/a");
        assertThat(output.getAll()).contains("Failed to fetch current temperature for city: Kyiv");
    }
}

