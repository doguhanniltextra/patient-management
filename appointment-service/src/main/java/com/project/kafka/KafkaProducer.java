package com.project.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.AppointmentKafkaResponseDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(AppointmentKafkaResponseDto appointment) {
        try {
            String json = objectMapper.writeValueAsString(appointment);
            kafkaTemplate.send("appointment-payment-updated", json);
            log.info("Sent JSON message to Kafka: {}", json);
        } catch (Exception e) {
            log.error("Failed to send message", e);
        }
    }
}

