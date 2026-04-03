package com.project.lab_service.service;

import com.project.lab_service.model.LabOutboxEvent;
import com.project.lab_service.repository.LabOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class LabOutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(LabOutboxPublisher.class);

    private final LabOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.lab-result-completed:lab-result-completed.v1}")
    private String labResultCompletedTopic;

    public LabOutboxPublisher(LabOutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    @Transactional
    public void publishPendingEvents() {
        List<LabOutboxEvent> pendingEvents = outboxRepository.findByStatus("PENDING");

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Found {} pending lab outbox events to publish.", pendingEvents.size());

        for (LabOutboxEvent event : pendingEvents) {
            String topic = determineTopic(event.getEventType());

            if (topic == null) {
                log.error("Unknown lab event type: {}. Marking as FAILED.", event.getEventType());
                event.setStatus("FAILED_UNKNOWN_TYPE");
                outboxRepository.save(event);
                continue;
            }

            try {
                // We use synchronous send or a future callback to ensure we only mark as published if Kafka ACKs
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayloadJson())
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            markAsPublished(event.getId());
                        } else {
                            log.error("Failed to publish lab event {} to Kafka: {}", event.getId(), ex.getMessage());
                            // Status remains PENDING for retry in next cycle
                        }
                    });
            } catch (Exception e) {
                log.error("Critical error while sending lab event {} to Kafka", event.getId(), e);
            }
        }
    }

    @Transactional
    public void markAsPublished(UUID eventId) {
        outboxRepository.findById(eventId).ifPresent(event -> {
            event.setStatus("PUBLISHED");
            outboxRepository.save(event);
            log.info("Lab event {} successfully published to Kafka.", eventId);
        });
    }

    private String determineTopic(String eventType) {
        if ("LAB_RESULT_COMPLETED".equals(eventType)) {
            return labResultCompletedTopic;
        }
        return null;
    }
}
