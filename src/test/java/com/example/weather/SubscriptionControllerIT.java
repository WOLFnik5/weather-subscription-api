package com.example.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class SubscriptionControllerIT {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("weather")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    static final GenericContainer<?> mailhog =
            new GenericContainer<>(DockerImageName.parse("mailhog/mailhog:latest"))
                    .withExposedPorts(1025, 8025); // 1025 SMTP, 8025 Web UI/API

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {

        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

         r.add("spring.test.database.replace", () -> "NONE");

        // Mail (MailHog)
        r.add("spring.mail.host", mailhog::getHost);
        r.add("spring.mail.port", () -> mailhog.getMappedPort(1025));
        r.add("spring.mail.protocol", () -> "smtp");
        r.add("spring.mail.properties.mail.smtp.auth", () -> "false");
        r.add("spring.mail.properties.mail.smtp.starttls.enable", () -> "false");

        // За потреби вимкни міграції у тестах:
//         r.add("spring.flyway.enabled", () -> "false");
    }


    @Autowired
    JavaMailSender mailSender;

    @Test
    void sendsEmailAndIsCapturedByMailHog() {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("test@example.com");
        message.setSubject("Test");
        message.setText("Hello");
        mailSender.send(message);


        RestTemplate rest = new RestTemplate();
        String api = "http://" + mailhog.getHost() + ":" + mailhog.getMappedPort(8025) + "/api/v2/messages";
        Map<?, ?> response = rest.getForObject(api, Map.class);
        List<?> items = (List<?>) response.get("items");
        assertThat(items).isNotEmpty();
    }
}
