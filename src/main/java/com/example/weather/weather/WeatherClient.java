package com.example.weather.weather;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class WeatherClient {
    private static final Logger logger = LoggerFactory.getLogger(WeatherClient.class);

    private final RestTemplate restTemplate;

    public WeatherClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchCurrentTemperature(String city) {
        try {
            String url = "https://wttr.in/" + URLEncoder.encode(city, StandardCharsets.UTF_8) + "?format=j1";
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
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

