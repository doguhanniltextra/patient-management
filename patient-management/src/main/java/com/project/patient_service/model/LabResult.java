package com.project.patient_service.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lab_result")
public class LabResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private UUID labOrderId;
    private String testCode;
    private String value;
    private String unit;
    private String referenceRange;
    private String abnormalFlag;
    private String reportPdfUrl;
    private Instant completedAt;
    private String eventId;

    public UUID getId() { return id; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public UUID getLabOrderId() { return labOrderId; }
    public void setLabOrderId(UUID labOrderId) { this.labOrderId = labOrderId; }
    public String getTestCode() { return testCode; }
    public void setTestCode(String testCode) { this.testCode = testCode; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getReferenceRange() { return referenceRange; }
    public void setReferenceRange(String referenceRange) { this.referenceRange = referenceRange; }
    public String getAbnormalFlag() { return abnormalFlag; }
    public void setAbnormalFlag(String abnormalFlag) { this.abnormalFlag = abnormalFlag; }
    public String getReportPdfUrl() { return reportPdfUrl; }
    public void setReportPdfUrl(String reportPdfUrl) { this.reportPdfUrl = reportPdfUrl; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
}
