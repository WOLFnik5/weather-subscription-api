package com.example.weather.controller;

import com.example.weather.model.Subscription;
import com.example.weather.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionRepository repository;

    @Test
    void createSubscription() throws Exception {
        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"city\":\"Kyiv\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.city").value("Kyiv"));

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void listSubscriptions() throws Exception {
        repository.save(Subscription.builder().email("a@example.com").city("Kyiv").build());
        repository.save(Subscription.builder().email("b@example.com").city("Lviv").build());

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("a@example.com"))
                .andExpect(jsonPath("$[1].email").value("b@example.com"));
    }

    @Test
    void deleteSubscription() throws Exception {
        Subscription subscription = repository.save(Subscription.builder()
                .email("test@example.com")
                .city("Kyiv")
                .build());

        mockMvc.perform(delete("/api/subscriptions/{id}", subscription.getId()))
                .andExpect(status().isNoContent());

        assertThat(repository.existsById(subscription.getId())).isFalse();
    }

    @Test
    void deleteNonExistingSubscriptionReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/subscriptions/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Subscription not found with id 999"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
    }

    @Test
    void duplicateSubscriptionReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"dup@example.com\",\"city\":\"Kyiv\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"dup@example.com\",\"city\":\"Kyiv\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Subscription already exists for this email and city"))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));

        assertThat(repository.count()).isEqualTo(1);
    }
}

