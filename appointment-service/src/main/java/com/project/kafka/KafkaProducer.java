package com.project.kafka;

import com.project.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import appointment.events.AppointmentEvent;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Appointment appointment) {
        AppointmentEvent appointmentEvent = AppointmentEvent
                .newBuilder()
                .setAmount(appointment.getAmount())
                .setDoctorId(appointment.getDoctorId())
                .setPatientId(appointment.getPatientId())
                .setPaymentStatus(appointment.isPaymentStatus())
                .build();

        log.info("SendEvent Active");
        CompletableFuture<SendResult<String, byte[]>> future =
                kafkaTemplate.send("appointment", appointmentEvent.toByteArray());

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
