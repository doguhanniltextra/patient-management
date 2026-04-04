package com.project.admission_service.dto;

import java.util.UUID;

public class AdmissionRequest {
    private UUID patientId;
    private UUID doctorId;
    private UUID wardId;

    public AdmissionRequest() {
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getWardId() {
        return wardId;
    }

    public void setWardId(UUID wardId) {
        this.wardId = wardId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }
}
