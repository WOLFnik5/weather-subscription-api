package com.example.weather.exception;

public class NotificationException extends RuntimeException {
    public NotificationException(String email, Throwable cause) {
        super("Notification failed for recipient: " + email, cause);
    }
}
