package com.project.patient_service.kafka;

import com.google.common.util.concurrent.ListenableFuture;
import com.project.patient_service.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        PatientEvent patientEvent = PatientEvent
                .newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType(String.valueOf(EventType.PATIENT_CREATED))
                .build();

        log.info("SendEvent Active");

        CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send("patient", patientEvent.toByteArray());

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully to Kafka, partition: {}, offset: {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Error sending message to Kafka", ex);
            }
        });
    }
}

