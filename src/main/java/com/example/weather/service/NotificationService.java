package com.example.weather.service;

import com.example.weather.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    @Qualifier("taskExecutor")
    private final Executor taskExecutor;

    @Value("${app.mail.from:no-reply@example.com}")
    private String mailFrom;

    @Async("taskExecutor")
    public CompletableFuture<Void> send(String email, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(mailFrom);
            mailMessage.setTo(email);
            mailMessage.setSubject("Weather update");
            mailMessage.setText(message);

            mailSender.send(mailMessage);
            return CompletableFuture.completedFuture(null);
        } catch (MailException e) {
            log.error("Failed to send notification to " + email, e);
            return CompletableFuture.failedFuture(new com.example.weather.exception.NotificationException(email, e));
        }
    }
}
