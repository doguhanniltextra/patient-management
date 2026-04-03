package com.project.notification_service.service;

import com.project.notification_service.model.NotificationHistory;
import com.project.notification_service.model.NotificationTemplate;
import com.project.notification_service.provider.NotificationProvider;
import com.project.notification_service.repository.NotificationHistoryRepository;
import com.project.notification_service.repository.NotificationTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationTemplateRepository templateRepository;
    private final NotificationHistoryRepository historyRepository;
    private final List<NotificationProvider> providers;
    private final TemplateService templateService;

    public NotificationService(NotificationTemplateRepository templateRepository,
                               NotificationHistoryRepository historyRepository,
                               List<NotificationProvider> providers,
                               TemplateService templateService) {
        this.templateRepository = templateRepository;
        this.historyRepository = historyRepository;
        this.providers = providers;
        this.templateService = templateService;
    }

    @Transactional
    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void processNotification(UUID patientId, String recipient, String templateCode, Map<String, Object> variables) {
        NotificationTemplate template = templateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateCode));

        String channel = template.getChannel();
        NotificationProvider provider = providers.stream()
                .filter(p -> p.getChannel().equalsIgnoreCase(channel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No provider found for channel: " + channel));

        String subject = templateService.process(template.getSubject(), variables);
        String body = templateService.process(template.getBody(), variables);

        NotificationHistory history = new NotificationHistory();
        history.setPatientId(patientId);
        history.setRecipient(recipient);
        history.setChannel(channel);
        history.setTemplateCode(templateCode);

        try {
            provider.send(recipient, subject, body);
            history.setStatus("SENT");
            log.info("Successfully sent {} notification to {}", channel, recipient);
        } catch (Exception e) {
            history.setStatus("FAILED");
            history.setErrorLog(e.getMessage());
            log.error("Failed to send {} notification to {}: {}", channel, recipient, e.getMessage());
            throw e; // Re-throw for retry
        } finally {
            historyRepository.save(history);
        }
    }
}
