package com.project.lab_service.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "test_results", schema = "lab_schema")
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID resultId;
    private UUID orderId;
    private String testCode;
    private String value;
    private String unit;
    private String referenceRange;
    private String abnormalFlag;
    private String reportPdfUrl;
    private Instant validatedAt;

    public UUID getResultId() { return resultId; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
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
    public Instant getValidatedAt() { return validatedAt; }
    public void setValidatedAt(Instant validatedAt) { this.validatedAt = validatedAt; }
}
