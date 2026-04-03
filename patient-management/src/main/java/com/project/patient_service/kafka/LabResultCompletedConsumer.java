package com.project.patient_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.patient_service.model.LabResult;
import com.project.patient_service.repository.LabResultRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class LabResultCompletedConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LabResultRepository labResultRepository;

    public LabResultCompletedConsumer(LabResultRepository labResultRepository) {
        this.labResultRepository = labResultRepository;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.LAB_RESULT_COMPLETED, groupId = KafkaTopics.PATIENT_LAB_RESULT_GROUP)
    public void consume(String message) throws Exception {
        JsonNode event = objectMapper.readTree(message);
        String eventId = event.get("eventId").asText();
        if (labResultRepository.findByEventId(eventId).isPresent()) {
            return;
        }
        UUID patientId = UUID.fromString(event.get("patientId").asText());
        UUID doctorId = UUID.fromString(event.get("doctorId").asText());
        UUID orderId = UUID.fromString(event.get("orderId").asText());
        Instant completedAt = Instant.parse(event.get("completedAt").asText());
        String reportPdfUrl = event.get("reportPdfUrl").asText();
        for (JsonNode item : event.get("results")) {
            String testCode = item.get("testCode").asText();
            if (labResultRepository.findByLabOrderIdAndTestCode(orderId, testCode).isPresent()) {
                continue;
            }
            LabResult result = new LabResult();
            result.setPatientId(patientId);
            result.setDoctorId(doctorId);
            result.setLabOrderId(orderId);
            result.setTestCode(testCode);
            result.setValue(item.get("value").asText());
            result.setUnit(item.get("unit").asText());
            result.setReferenceRange(item.get("referenceRange").asText());
            result.setAbnormalFlag(item.get("abnormalFlag").asText());
            result.setReportPdfUrl(reportPdfUrl);
            result.setCompletedAt(completedAt);
            result.setEventId(eventId);
            labResultRepository.save(result);
        }
    }
}
