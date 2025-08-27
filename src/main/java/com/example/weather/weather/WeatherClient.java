package com.example.weather.weather;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class WeatherClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchCurrentTemperature(String city) {
        try {
            String url = "https://wttr.in/" + URLEncoder.encode(city, StandardCharsets.UTF_8) + "?format=j1";
            Map response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return "n/a";
            }
            List current = (List) response.get("current_condition");
            if (current != null && !current.isEmpty()) {
                Object temp = ((Map) current.get(0)).get("temp_C");
                return temp != null ? temp.toString() : "n/a";
            }
        } catch (Exception ignored) {
        }
        return "n/a";
    }
}
