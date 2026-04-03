package com.project.admission_service.service;

import com.project.admission_service.model.AdmissionOutboxEvent;
import com.project.admission_service.repository.AdmissionOutboxRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class AdmissionOutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(AdmissionOutboxPublisher.class);

    private final AdmissionOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.admission-bed-charge}")
    private String bedChargeTopic;

    @Value("${kafka.topic.admission-discharged}")
    private String patientDischargedTopic;

    public AdmissionOutboxPublisher(
            AdmissionOutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    @Transactional
    public void publishEvents() {
        List<AdmissionOutboxEvent> pendingEvents = outboxRepository.findByStatus("PENDING");
        
        if (pendingEvents.isEmpty()) {
            return;
        }

        log.debug("Found {} pending outbox events to publish.", pendingEvents.size());

        for (AdmissionOutboxEvent event : pendingEvents) {
            String topic = determineTopic(event.getEventType());
            
            if (topic == null) {
                log.error("Unknown event type: {}. Skipping event: {}", event.getEventType(), event.getId());
                event.setStatus("FAILED_UNKNOWN_TYPE");
                outboxRepository.save(event);
                continue;
            }

            try {
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayloadJson())
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            markAsPublished(event.getId());
                        } else {
                            log.error("Failed to publish event to Kafka: {}", event.getId(), ex);
                        }
                    });
            } catch (Exception e) {
                log.error("Error sending message to Kafka for event: {}", event.getId(), e);
            }
        }
    }

    @Transactional
    public void markAsPublished(java.util.UUID eventId) {
        outboxRepository.findById(eventId).ifPresent(event -> {
            event.setStatus("PUBLISHED");
            outboxRepository.save(event);
        });
    }

    private String determineTopic(String eventType) {
        return switch (eventType) {
            case "DAILY_BED_CHARGE" -> bedChargeTopic;
            case "PATIENT_DISCHARGED" -> patientDischargedTopic;
            default -> null;
        };
    }
}
