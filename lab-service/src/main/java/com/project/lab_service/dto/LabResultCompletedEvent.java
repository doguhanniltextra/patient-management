package com.project.lab_service.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class LabResultCompletedEvent {
    public String eventId;
    public String eventVersion;
    public Instant occurredAt;
    public UUID orderId;
    public UUID patientId;
    public String patientEmail;
    public String patientPhone;
    public UUID doctorId;
    public List<ResultItem> results;
    public String reportPdfUrl;
    public Instant completedAt;
    public String correlationId;

    public static class ResultItem {
        public String testCode;
        public String value;
        public String unit;
        public String referenceRange;
        public String abnormalFlag;
    }
}
