package com.project.utils;

import com.project.config.RestTemplateAddresses;
import com.project.dto.PatientInfoDTO;
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
        return fetchPatientInfo(patientId) != null;
    }

    @CircuitBreaker(name = "patientService", fallbackMethod = "patientInfoFallback")
    @Retry(name = "patientService")
    public PatientInfoDTO fetchPatientInfo(UUID patientId) {
        ResponseEntity<PatientInfoDTO> response =
                restTemplate.getForEntity(PATIENT_SERVICE_URL + patientId, PatientInfoDTO.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return null;
    }

    @CircuitBreaker(name = "doctorService", fallbackMethod = "doctorFallback")
    @Retry(name = "doctorService")
    public boolean checkDoctorExists(UUID doctorId) {
        ResponseEntity<java.util.Map> response =
                restTemplate.getForEntity(DOCTOR_SERVICE_URL + doctorId, java.util.Map.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Boolean isAvailable = (Boolean) response.getBody().get("available");
            if (Boolean.FALSE.equals(isAvailable)) {
                throw new com.project.exception.CustomConflictException("Doctor is not available for booking.");
            }
            return true;
        }
        return false;
    }

    @CircuitBreaker(name = "doctorService", fallbackMethod = "voidFallback")
    @Retry(name = "doctorService")
    public void increaseDoctorPatientCount(UUID doctorId) {
        restTemplate.put(DOCTOR_SERVICE_URL + doctorId + "/increase-patient", null);
    }

    // Fallback: circuit is open or all retries exhausted → fail clearly
    private boolean patientFallback(UUID patientId, Throwable t) {
        log.error("Circuit breaker OPEN for patient-service. Cannot verify patient {}. Cause: {}", patientId, t.getMessage());
        return false;
    }

    private PatientInfoDTO patientInfoFallback(UUID patientId, Throwable t) {
        log.error("Circuit breaker OPEN for patient-service. Cannot fetch patient {} details. Cause: {}", patientId, t.getMessage());
        if (t instanceof HttpClientErrorException.NotFound) {
            return null;
        }
        return null;
    }

    private boolean doctorFallback(UUID doctorId, Throwable t) {
        log.error("Circuit breaker OPEN for doctor-service. Cannot verify doctor {}. Cause: {}", doctorId, t.getMessage());
        if (t instanceof com.project.exception.CustomConflictException) {
            throw (com.project.exception.CustomConflictException) t;
        }
        return false;
    }

    private void voidFallback(UUID doctorId, Throwable t) {
        log.error("Circuit breaker OPEN for doctor-service. Cannot increase patient count for doctor {}. Cause: {}", doctorId, t.getMessage());
        throw new com.project.exception.CustomConflictException("Failed to increase doctor patient count: " + t.getMessage());
    }
}
