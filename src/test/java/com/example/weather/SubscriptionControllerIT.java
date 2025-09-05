package com.example.weather;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mailhog.MailHogContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = SubscriptionControllerIT.TestConfig.class)
class SubscriptionControllerIT {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestConfig {
    }

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static final MailHogContainer mailhog = new MailHogContainer();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.mail.host", mailhog::getHost);
        registry.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JavaMailSender mailSender;

    @Test
    void shouldSendEmailThroughMailhog() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(result).isEqualTo(1);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("test@example.com");
        message.setSubject("Test");
        message.setText("Hello");
        mailSender.send(message);

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://" + mailhog.getHost() + ":" + mailhog.getMappedPort(8025) + "/api/v2/messages";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<?> items = (List<?>) response.get("items");
        assertThat(items).isNotEmpty();
    }
}

