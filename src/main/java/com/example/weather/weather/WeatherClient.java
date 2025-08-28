package com.example.weather.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class WeatherClient {
    private static final Logger logger = LoggerFactory.getLogger(WeatherClient.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String fetchCurrentTemperature(String city) {
        try {
            String url = "https://wttr.in/" + URLEncoder.encode(city, StandardCharsets.UTF_8) + "?format=j1";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return "n/a";
            }
            List<Map<String, Object>> current = (List<Map<String, Object>>) response.get("current_condition");
            if (current != null && !current.isEmpty()) {
                Object temp = current.get(0).get("temp_C");
                return temp != null ? temp.toString() : "n/a";
            }
        } catch (Exception e) {
            logger.error("Failed to fetch current temperature for city: {}", city, e);
        }
        return "n/a";
    }
}
