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

    /**
     * Sends an Appointment event message to the Kafka topic "appointment".
     * <p>
     * Constructs an {@link AppointmentEvent} protobuf message from the given
     * {@link Appointment} object, serializes it to a byte array, and sends it asynchronously.
     * Logs the success or failure of the send operation.
     *
     * @param appointment the Appointment object containing event data to send
     */
    public void sendEvent(Appointment appointment) {
        AppointmentEvent appointmentEvent = AppointmentEvent
                .newBuilder()
                .setAmount(appointment.getAmount())
                .setDoctorId(String.valueOf(appointment.getDoctorId()))
                .setPatientId(String.valueOf(appointment.getPatientId()))
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
