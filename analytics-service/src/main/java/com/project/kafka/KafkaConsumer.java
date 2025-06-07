package com.project.kafka;


import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;


@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            // analytics business and so on

            log.info("Received Patient Event:[PatientId={}, PatientName={}]", patientEvent.getPatientId(), patientEvent.getName());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }


}
