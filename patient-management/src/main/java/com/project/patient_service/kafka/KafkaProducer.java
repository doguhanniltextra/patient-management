package com.project.patient_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.patient_service.dto.request.KafkaPatientRequestDto;
import com.project.patient_service.helper.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserValidator userValidator;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, UserValidator userValidator) {
        this.kafkaTemplate = kafkaTemplate;
        this.userValidator = userValidator;
    }

    public void sendEvent(KafkaPatientRequestDto kafkaPatientRequestDto) {
        try {
            log.info("KAFKA: Kafka -PATIENT- Triggered");
            Map<String, Object> event = userValidator.getStringObjectMap(kafkaPatientRequestDto);

            log.info("KAFKA: kafka -PATIENT-{}", event);

            String json = objectMapper.writeValueAsString(event);

            userValidator.sendKafkaEvent(json, kafkaTemplate);
            log.info("Sent JSON message to Kafka: {}", json);
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }
}
