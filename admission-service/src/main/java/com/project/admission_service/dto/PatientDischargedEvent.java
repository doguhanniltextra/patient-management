package com.project.admission_service.dto;

import java.util.UUID;

public class PatientDischargedEvent {
    private String eventId;
    private UUID patientId;
    private UUID admissionId;

    public PatientDischargedEvent() {
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
}
