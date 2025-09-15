package com.example.weather.controller;

import org.springframework.context.annotation.Import;

import com.example.weather.exception.BadRequestException;
import com.example.weather.exception.NotFoundException;
import com.example.weather.entity.Subscription;
import com.example.weather.dto.request.SubscriptionRequest;
import com.example.weather.service.SubscriptionService;
import com.example.weather.mapper.SubscriptionMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(controllers = SubscriptionController.class)
@Import(SubscriptionMapperImpl.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionService service;

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

        // стаби для сервісу
        when(service.create(any(SubscriptionRequest.class))).thenReturn(saved);

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

    @Test
    void createSubscriptionInvalidBody() throws Exception {
        String reqJson = """
          {"email":"","city":"Kyiv"}
        """;

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.email[0]").value("must not be blank"));
    }

    @Test
    void createSubscriptionDuplicate() throws Exception {
        String reqJson = """
          {"email":"test@example.com","city":"Kyiv"}
        """;

        when(service.create(any(SubscriptionRequest.class)))
                .thenThrow(new BadRequestException("Subscription already exists for this email and city"));

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Subscription already exists for this email and city"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void listSubscriptions() throws Exception {
        Subscription s1 = new Subscription();
        s1.setId(1L);
        s1.setEmail("user1@example.com");
        s1.setCity("Kyiv");

        Subscription s2 = new Subscription();
        s2.setId(2L);
        s2.setEmail("user2@example.com");
        s2.setCity("Lviv");

        Page<Subscription> page = new PageImpl<>(List.of(s1, s2), PageRequest.of(0, 2), 2);

        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/subscriptions")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].city").value("Lviv"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void listSubscriptionsInvalidSize() throws Exception {
        mockMvc.perform(get("/api/subscriptions")
                        .param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Page size must be between 1 and 100"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void listSubscriptionsNegativePage() throws Exception {
        mockMvc.perform(get("/api/subscriptions")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail", containsString("must not be less than zero")))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deleteSubscription() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/subscriptions/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSubscriptionNotFound() throws Exception {
        doThrow(new NotFoundException("Subscription not found with id 99"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/subscriptions/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Subscription not found with id 99"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
