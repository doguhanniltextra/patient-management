package com.project.notification_service.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockEmailProvider implements NotificationProvider {
    private static final Logger log = LoggerFactory.getLogger(MockEmailProvider.class);

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("Sending EMAIL to: {} | Subject: {} | Content: {}", recipient, subject, body);
        // Simulate real SMTP delay or potential transient error
        if (recipient.contains("fail")) {
            throw new RuntimeException("Simulated SMTP failure for " + recipient);
        }
    }

    @Override
    public String getChannel() {
        return "EMAIL";
    }
}
