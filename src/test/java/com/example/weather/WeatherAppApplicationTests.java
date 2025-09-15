package com.example.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class WeatherAppApplicationTests {

    @MockitoBean
    @SuppressWarnings("unused")
    private JavaMailSender unusedMailSender;

    @Test
    void contextLoads() {
        // Smoke test: passes if the Spring ApplicationContext starts successfully.
        // Any exception during context initialization will fail this test, so no assertions are required.
    }

}
