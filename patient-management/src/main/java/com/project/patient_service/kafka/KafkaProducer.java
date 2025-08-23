package com.project.patient_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.patient_service.dto.request.KafkaPatientRequestDto;
import com.project.patient_service.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(KafkaPatientRequestDto kafkaPatientRequestDto) {
        try {
            log.info("KAFKA: Kafka -PATIENT- Triggered");
            Map<String, Object> event = new HashMap<>();
            event.put("patientId", kafkaPatientRequestDto.getId().toString());
            event.put("name", kafkaPatientRequestDto.getName());
            event.put("email", kafkaPatientRequestDto.getEmail());
            event.put("eventType", "PATIENT_CREATED");

            log.info("KAFKA: kafka -PATIENT-{}", event);

            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send("patient", json);
            log.info("Sent JSON message to Kafka: {}", json);
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }
}
