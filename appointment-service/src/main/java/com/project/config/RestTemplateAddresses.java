package com.project.config;

import org.springframework.stereotype.Component;

@Component
public class RestTemplateAddresses {
    private final String PATIENT_SERVICE_URL = "http://patient-service:8080/patients/find/";
    private final String DOCTOR_SERVICE_URL = "http://doctor-service:8083/doctors/find/";

    public String getPATIENT_SERVICE_URL() {
        return PATIENT_SERVICE_URL;
    }

    public String getDOCTOR_SERVICE_URL() {
        return DOCTOR_SERVICE_URL;
    }
}
