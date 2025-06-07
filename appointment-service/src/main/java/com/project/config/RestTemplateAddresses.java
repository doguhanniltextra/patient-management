package com.project.config;

public class RestTemplateAddresses {
      String PATIENT_SERVICE_URL = "http://localhost:4004/api/patients/";
      String DOCTOR_SERVICE_URL = "http://localhost:4004/api/doctors/";

    public String getPATIENT_SERVICE_URL() {
        return PATIENT_SERVICE_URL;
    }

    public String getDOCTOR_SERVICE_URL() {
        return DOCTOR_SERVICE_URL;
    }
}
