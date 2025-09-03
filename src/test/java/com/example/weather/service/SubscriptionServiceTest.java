package com.example.weather.service;

import com.example.weather.model.SubscriptionRequest;
import com.example.weather.repository.SubscriptionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SubscriptionServiceTest {

    @Autowired
    private SubscriptionService service;

    @Autowired
    private SubscriptionRepository repo;

    @Autowired
    private EntityManager em;

    @MockBean
    private JavaMailSender mailSender;

    @AfterEach
    void cleanup() {
        em.clear();
        repo.deleteAll();
    }

    @Test
    void createThenDuplicate_throwsAndKeepsOnlyOneRow() {
        var req = new SubscriptionRequest("test@example.com", "Kyiv");

        service.create(req);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOfAny(DataIntegrityViolationException.class, RuntimeException.class);

        em.clear();

        assertThat(repo.count()).isEqualTo(1);
    }
    @Test
    void concurrentDuplicates_onlyOneSucceeds() throws Exception {
                var req = new SubscriptionRequest("test@example.com", "Kyiv");

                        var pool = Executors.newFixedThreadPool(2);
                Callable<Boolean> task = () -> {
                        try {
                                service.create(req);
                                return true;
                            } catch (RuntimeException ex) {

                                        return false;
                            }
                    };

                        Future<Boolean> f1 = pool.submit(task);
                Future<Boolean> f2 = pool.submit(task);

                        boolean r1 = f1.get();
                boolean r2 = f2.get();


                                assertThat(r1 ^ r2).isTrue();

                        em.clear();
                assertThat(repo.count()).isEqualTo(1);
            }


}
