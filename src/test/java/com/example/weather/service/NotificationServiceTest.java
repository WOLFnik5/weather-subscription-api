package com.example.weather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(mailSender);
        ReflectionTestUtils.setField(service, "mailFrom", "sender@example.com");
    }

    @Test
    void sendSetsFromAddress() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        service.send("to@example.com", "msg");

        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getFrom()).isEqualTo("sender@example.com");
    }

    @Test
    void nonMailExceptionsAreRethrown() {
        RuntimeException ex = new RuntimeException("boom");
        doThrow(ex).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class, () -> service.send("to@example.com", "msg"));
    }
}

