package com.project.lab_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.lab_service.constants.KafkaTopics;
import com.project.lab_service.dto.LabResultCompletedEvent;
import com.project.lab_service.model.LabOrder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class LabResultEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LabResultEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(LabOrder order, List<LabResultCompletedEvent.ResultItem> items, String reportUrl, String correlationId) {
        LabResultCompletedEvent event = new LabResultCompletedEvent();
        event.eventId = UUID.randomUUID().toString();
        event.eventVersion = "v1";
        event.occurredAt = Instant.now();
        event.orderId = order.getOrderId();
        event.patientId = order.getPatientId();
        event.patientEmail = order.getPatientEmail();
        event.patientPhone = order.getPatientPhone();
        event.doctorId = order.getDoctorId();
        event.results = items;
        event.reportPdfUrl = reportUrl;
        event.completedAt = order.getCompletedAt();
        event.correlationId = correlationId;
        try {
            kafkaTemplate.send(KafkaTopics.LAB_RESULT_COMPLETED, order.getOrderId().toString(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize lab result event", e);
        }
    }
}
