package com.example.weather.service;

import com.example.weather.exception.BadRequestException;
import com.example.weather.dto.request.SubscriptionRequest;
import com.example.weather.repository.SubscriptionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SubscriptionServiceTest {

    private final SubscriptionService service;
    private final SubscriptionRepository repo;
    private final EntityManager em;

    @MockitoBean
    @SuppressWarnings("unused")
    private JavaMailSender mailSender;

    @Autowired
    SubscriptionServiceTest(SubscriptionService service,
                            SubscriptionRepository repo,
                            EntityManager em) {
        this.service = service;
        this.repo = repo;
        this.em = em;
    }

    @AfterEach
    void cleanup() {
        em.clear();
        repo.deleteAll();
    }

    @Test
    void createThenDuplicate_throwsAndKeepsOnlyOneRow() {
        var req = new SubscriptionRequest("test@example.com", "Kyiv");

        service.create(req);

        assertThrows(BadRequestException.class, () -> service.create(req));

        em.clear();

        assertThat(repo.count()).isEqualTo(1);
    }

    @Test
    void concurrentDuplicates_onlyOneSucceeds() throws Exception {
        var req = new SubscriptionRequest("test@example.com", "Kyiv");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Callable<Boolean> task = () -> {
                try {
                    service.create(req);
                    return true;
                } catch (BadRequestException ex) {
                    return false;
                }
            };

            Future<Boolean> first = pool.submit(task);
            Future<Boolean> second = pool.submit(task);

            boolean firstResult = first.get();
            boolean secondResult = second.get();

            assertThat(firstResult ^ secondResult).isTrue();
        } finally {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException ex) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
                throw ex;
            }
        }

        em.clear();
        assertThat(repo.count()).isEqualTo(1);
    }
}
