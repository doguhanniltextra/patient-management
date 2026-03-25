package com.project.utils;

import com.project.config.RestTemplateAddresses;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Component
public class IdValidation {

    private static final Logger log = LoggerFactory.getLogger(IdValidation.class);

    private final String PATIENT_SERVICE_URL;
    private final String DOCTOR_SERVICE_URL;
    private final RestTemplate restTemplate;

    public IdValidation(RestTemplateAddresses restTemplateAddresses, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.PATIENT_SERVICE_URL = restTemplateAddresses.getPATIENT_SERVICE_URL();
        this.DOCTOR_SERVICE_URL = restTemplateAddresses.getDOCTOR_SERVICE_URL();
    }

    @CircuitBreaker(name = "patientService", fallbackMethod = "patientFallback")
    @Retry(name = "patientService")
    public boolean checkPatientExists(UUID patientId) {
        ResponseEntity<Object> response =
                restTemplate.getForEntity(PATIENT_SERVICE_URL + patientId, Object.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @CircuitBreaker(name = "doctorService", fallbackMethod = "doctorFallback")
    @Retry(name = "doctorService")
    public boolean checkDoctorExists(UUID doctorId) {
        ResponseEntity<Object> response =
                restTemplate.getForEntity(DOCTOR_SERVICE_URL + doctorId, Object.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    // Fallback: circuit is open or all retries exhausted → fail clearly
    private boolean patientFallback(UUID patientId, Throwable t) {
        log.error("Circuit breaker OPEN for patient-service. Cannot verify patient {}. Cause: {}", patientId, t.getMessage());
        return false;
    }

    private boolean doctorFallback(UUID doctorId, Throwable t) {
        log.error("Circuit breaker OPEN for doctor-service. Cannot verify doctor {}. Cause: {}", doctorId, t.getMessage());
        return false;
    }
}
