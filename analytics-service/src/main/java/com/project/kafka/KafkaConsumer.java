package com.project.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(String event) { 
        try {
         
            Map<String, Object> patientEvent = objectMapper.readValue(event, Map.class);

            String patientId = (String) patientEvent.get("patientId");
            String name = (String) patientEvent.get("name");
            String email = (String) patientEvent.get("email");
            String eventType = (String) patientEvent.get("eventType");

     
            log.info("Received Patient Event:[PatientId={}, PatientName={}, EventType={}]", patientId, name,  email, eventType);
        } catch (Exception e) {
            log.error("Failed to consume Kafka event", e);
        }
    }
}
