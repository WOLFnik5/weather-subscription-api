package com.example.weather.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@example.com}")
    private String mailFrom;

    @Async
    public CompletableFuture<Void> send(String email, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(email);
        mailMessage.setSubject("Weather update");
        mailMessage.setText(message);
        try {
            mailSender.send(mailMessage);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Failed to send notification to {}", email, e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
