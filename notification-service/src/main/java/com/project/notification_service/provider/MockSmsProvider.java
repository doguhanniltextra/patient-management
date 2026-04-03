package com.project.notification_service.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockSmsProvider implements NotificationProvider {
    private static final Logger log = LoggerFactory.getLogger(MockSmsProvider.class);

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("Sending SMS to: {} | Content: {}", recipient, body);
        if (recipient.contains("fail")) {
            throw new RuntimeException("Simulated SMS Gateway failure for " + recipient);
        }
    }

    @Override
    public String getChannel() {
        return "SMS";
    }
}
