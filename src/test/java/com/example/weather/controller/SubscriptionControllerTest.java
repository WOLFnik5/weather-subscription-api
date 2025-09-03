package com.example.weather.controller;

import com.example.weather.model.Subscription;
import com.example.weather.model.SubscriptionDto;
import com.example.weather.model.SubscriptionRequest;
import com.example.weather.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService service;

    // Щоб контекст зібрався, якщо NotificationService підтягує JavaMailSender
    @MockBean
    private JavaMailSender mailSender;

    @Test
    void createSubscription() throws Exception {
        // вхідний JSON
        String reqJson = """
          {"email":"test@example.com","city":"Kyiv"}
        """;

        // готуємо об’єкти
        Subscription saved = new Subscription();
        saved.setId(1L);
        saved.setEmail("test@example.com");
        saved.setCity("Kyiv");

        SubscriptionDto dto = new SubscriptionDto(1L, "test@example.com", "Kyiv");

        // стаби для сервісу
        when(service.create(any(SubscriptionRequest.class))).thenReturn(saved);
        when(service.toDto(saved)).thenReturn(dto);

        // виклик і перевірки
        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.city").value("Kyiv"));
    }
}
