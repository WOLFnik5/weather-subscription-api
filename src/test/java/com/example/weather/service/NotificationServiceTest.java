package com.example.weather.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "app.mail.from=sender@example.com")
class NotificationServiceTest {

    @Autowired
    private NotificationService service;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void sendSetsFromAddress() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        service.send("to@example.com", "msg").join();

        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getFrom()).isEqualTo("sender@example.com");
    }

    @Test
    void nonMailExceptionsCompleteFutureExceptionally() {
        RuntimeException ex = new RuntimeException("boom");
        doThrow(ex).when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> service.send("to@example.com", "msg").join())
                .hasCause(ex);
    }

    @Test
    void logContainsRecipientWhenSendFails() {
        MailSendException ex = new MailSendException("boom");
        doThrow(ex).when(mailSender).send(any(SimpleMailMessage.class));

        Logger logger = (Logger) LoggerFactory.getLogger(NotificationService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        assertThatThrownBy(() -> service.send("to@example.com", "msg").join())
                .hasCause(ex);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(msg -> msg.contains("to@example.com"));

        logger.detachAppender(listAppender);
    }

    @Test
    void sendExecutesAsynchronously() {
        String callingThread = Thread.currentThread().getName();

        String asyncThread = service.send("to@example.com", "msg")
                .thenApply(v -> Thread.currentThread().getName())
                .join();

        assertThat(asyncThread).isNotEqualTo(callingThread);
    }
}

