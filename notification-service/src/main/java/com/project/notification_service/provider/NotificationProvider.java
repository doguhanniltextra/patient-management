package com.project.notification_service.provider;

public interface NotificationProvider {
    void send(String recipient, String subject, String body);
    String getChannel();
}
