package com.project.admission_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class DailyBedChargeEvent {
    private String eventId;
    private UUID patientId;
    private UUID admissionId;
    private BigDecimal amount;
    private String currency;

    public DailyBedChargeEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getAdmissionId() {
        return admissionId;
    }

    public void setAdmissionId(UUID admissionId) {
        this.admissionId = admissionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
