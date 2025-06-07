package com.project.utils;

import com.project.config.RestTemplateAddresses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Component
public class IdValidation {

    private final String PATIENT_SERVICE_URL;
    private final String DOCTOR_SERVICE_URL;
    private final RestTemplate restTemplate;

    public IdValidation(RestTemplateAddresses restTemplateAddresses, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.PATIENT_SERVICE_URL = restTemplateAddresses.getPATIENT_SERVICE_URL();
        this.DOCTOR_SERVICE_URL = restTemplateAddresses.getDOCTOR_SERVICE_URL();
    }

    public boolean checkPatientExists(UUID patientId) {
        try {
            ResponseEntity<Void> response =
                    restTemplate.getForEntity(PATIENT_SERVICE_URL + patientId, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    public boolean checkDoctorExists(UUID doctorId) {
        try {
            ResponseEntity<Void> response =
                    restTemplate.getForEntity(DOCTOR_SERVICE_URL + doctorId, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}
