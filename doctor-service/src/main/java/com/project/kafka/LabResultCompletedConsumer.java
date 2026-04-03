package com.project.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.constants.KafkaTopics;
import com.project.model.DoctorNotification;
import com.project.repository.DoctorNotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class LabResultCompletedConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DoctorNotificationRepository notificationRepository;

    public LabResultCompletedConsumer(DoctorNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = KafkaTopics.LAB_RESULT_COMPLETED, groupId = KafkaTopics.DOCTOR_LAB_RESULT_GROUP)
    public void consume(String message) throws Exception {
        JsonNode event = objectMapper.readTree(message);
        String eventId = event.get("eventId").asText();
        if (notificationRepository.findByEventId(eventId).isPresent()) {
            return;
        }
        DoctorNotification notification = new DoctorNotification();
        notification.setDoctorId(UUID.fromString(event.get("doctorId").asText()));
        notification.setEventId(eventId);
        notification.setCreatedAt(Instant.now());
        notification.setMessage("Lab result completed for order " + event.get("orderId").asText());
        notificationRepository.save(notification);
    }
}
