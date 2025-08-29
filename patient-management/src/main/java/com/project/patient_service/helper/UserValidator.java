package com.project.patient_service.helper;

import com.project.patient_service.dto.request.CreatePatientServiceRequestDto;
import com.project.patient_service.dto.request.KafkaPatientRequestDto;
import com.project.patient_service.dto.request.UpdatePatientServiceRequestDto;
import com.project.patient_service.exception.EmailAlreadyExistsException;
import com.project.patient_service.exception.PatientNotFoundException;
import com.project.patient_service.model.Patient;
import com.project.patient_service.repository.PatientRepository;
import com.project.patient_service.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserValidator {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    public void CheckEmailIsExistsOrNotForCreatePatient(CreatePatientServiceRequestDto patientRequestDTO, PatientRepository patientRepository) {
        if( patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            log.error("PATIENT: A patient with this email already exists");
            throw new EmailAlreadyExistsException("A patient with this email already exists.");
        }
    }
    public Patient getPatientForCreatePatient(CreatePatientServiceRequestDto patientRequestDTO) {
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(parseDateForCreatePatient(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(parseDateForCreatePatient(patientRequestDTO.getRegisteredDate()));
        return patient;
    }
    private LocalDate parseDateForCreatePatient(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr);
        }
    }
    public void checkEmailIsExistsOrNotForUpdatePatient(UUID id, UpdatePatientServiceRequestDto updatePatientServiceRequestDto, PatientRepository patientRepository) {
        if( patientRepository.existsByEmailAndIdNot(updatePatientServiceRequestDto.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient with this email already exists.");
        }
    }
    public Patient getPatientForUpdateMethod(UUID id, PatientRepository patientRepository) {
        Patient patient = patientRepository
                .findById(id)
                .orElseThrow(()-> new PatientNotFoundException("Patient Not Found With This ID"));
        return patient;
    }
    public static ResponseEntity<Patient> getPatientResponseEntity(Optional<Patient> currentId) {
        if (currentId.isPresent()) {
            return ResponseEntity.ok(currentId.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    public static ResponseEntity<Boolean> getBooleanResponseEntity(boolean patientByEmail) {
        if(patientByEmail) {
            return ResponseEntity.ok().body(true);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
    public boolean isPatientByEmail(String email, PatientService patientService) {
        boolean patientByEmail = patientService.findPatientByEmail(email);
        return patientByEmail;
    }
    public static Map<String, Object> getStringObjectMap(KafkaPatientRequestDto kafkaPatientRequestDto) {
        Map<String, Object> event = new HashMap<>();
        event.put("patientId", kafkaPatientRequestDto.getId().toString());
        event.put("name", kafkaPatientRequestDto.getName());
        event.put("email", kafkaPatientRequestDto.getEmail());
        event.put("eventType", "PATIENT_CREATED");
        return event;
    }
    public void sendKafkaEvent(String json, KafkaTemplate kafkaTemplate) {
        kafkaTemplate.send("patient", json);
    }
}
