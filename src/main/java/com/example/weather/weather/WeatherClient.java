package com.example.weather.weather;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WeatherClient {
    private static final Logger logger = LoggerFactory.getLogger(WeatherClient.class);

    private final RestTemplate restTemplate;

    public WeatherClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String fetchCurrentTemperature(String city) {
        try {
            var uri = UriComponentsBuilder.fromHttpUrl("https://wttr.in/{city}")
                    .queryParam("format", "j1")
                    .buildAndExpand(city)
                    .toUri();
            JsonNode response = restTemplate.getForObject(uri, JsonNode.class);
            if (response == null) {
                return "n/a";
            }
            JsonNode current = response.path("current_condition");
            if (current.isArray() && current.size() > 0) {
                JsonNode temp = current.get(0).path("temp_C");
                if (!temp.isMissingNode()) {
                    return temp.asText();
                }
            }
        } catch (Exception e) {
            logger.error("Failed to fetch current temperature for city: {}", city, e);
        }
        return "n/a";
    }
}

