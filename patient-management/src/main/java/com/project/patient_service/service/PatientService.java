package com.project.patient_service.service;

import com.project.patient_service.dto.response.GetPatientServiceResponseDto;
import com.project.patient_service.dto.request.KafkaPatientRequestDto;
import com.project.patient_service.dto.response.UpdatePatientServiceResponseDto;
import com.project.patient_service.dto.request.CreatePatientServiceRequestDto;
import com.project.patient_service.dto.request.UpdatePatientServiceRequestDto;
import com.project.patient_service.exception.EmailAlreadyExistsException;
import com.project.patient_service.exception.PatientNotFoundException;

import com.project.patient_service.kafka.KafkaProducer;
import com.project.patient_service.model.Patient;
import com.project.patient_service.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final KafkaProducer kafkaProducer;

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    public PatientService(PatientRepository patientRepository, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public List<GetPatientServiceResponseDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<GetPatientServiceResponseDto> result = patients
                .stream()
                .map(patient -> {
                    GetPatientServiceResponseDto getPatientServiceResponseDtos = new GetPatientServiceResponseDto();
                    getPatientServiceResponseDtos.setId(patient.getId());
                    getPatientServiceResponseDtos.setEmail(patient.getEmail());
                    getPatientServiceResponseDtos.setAddress(patient.getAddress());
                    getPatientServiceResponseDtos.setDateOfBirth(patient.getDateOfBirth());
                    getPatientServiceResponseDtos.setName(patient.getName());

                    return getPatientServiceResponseDtos;
                }).toList();

        return result;
    }

    public UpdatePatientServiceResponseDto createPatient(CreatePatientServiceRequestDto patientRequestDTO) throws EmailAlreadyExistsException {
        if( patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            log.error("PATIENT: A patient with this email already exists");
            throw new EmailAlreadyExistsException("A patient with this email already exists.");
        }

        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));

        Patient newPatient = patientRepository.save(patient);
        log.info("PATIENT: Patient ID -> {}", newPatient.getId());


        // KAFKA DTO
        KafkaPatientRequestDto kafkaPatientRequestDto = new KafkaPatientRequestDto();
        kafkaPatientRequestDto.setId(UUID.fromString(newPatient.getId()));
        kafkaPatientRequestDto.setEmail(newPatient.getEmail());
        kafkaPatientRequestDto.setName(newPatient.getName());
        kafkaProducer.sendEvent(kafkaPatientRequestDto);

        // RESPONSE DTO
        UpdatePatientServiceResponseDto updatePatientServiceResponseDto = new UpdatePatientServiceResponseDto();
        updatePatientServiceResponseDto.setId(patient.getId());
        updatePatientServiceResponseDto.setName(patient.getName());
        updatePatientServiceResponseDto.setEmail(patient.getEmail());
        updatePatientServiceResponseDto.setAddress(patient.getAddress());
        updatePatientServiceResponseDto.setDateOfBirth(patient.getDateOfBirth());
        return updatePatientServiceResponseDto;
    }

    public UpdatePatientServiceResponseDto updatePatient(UUID id, UpdatePatientServiceRequestDto updatePatientServiceRequestDto) {
        Patient patient = patientRepository
                .findById(id)
                .orElseThrow(()-> new PatientNotFoundException("Patient Not Found With This ID"));
        if( patientRepository.existsByEmailAndIdNot(updatePatientServiceRequestDto.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient with this email already exists.");
        }
        log.info("PATIENT: Update service triggered");



        patient.setName(updatePatientServiceRequestDto.getName());
        patient.setAddress(updatePatientServiceRequestDto.getAddress());
        patient.setEmail(updatePatientServiceRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(updatePatientServiceRequestDto.getDateOfBirth()));
        log.info("PATIENT: Patient ID -> {}" , patient.getId());


        Patient updatedPatient = patientRepository.save(patient);
        log.info("PATIENT: Update service is done");

        UpdatePatientServiceResponseDto updatePatientServiceResponseDto = new UpdatePatientServiceResponseDto();
        updatePatientServiceResponseDto.setName(updatedPatient.getName());
        updatePatientServiceResponseDto.setAddress(updatedPatient.getAddress());
        updatePatientServiceResponseDto.setDateOfBirth(updatedPatient.getDateOfBirth());
        updatePatientServiceResponseDto.setEmail(updatedPatient.getEmail());

        return updatePatientServiceResponseDto;
    }

    public void deletePatient(UUID id) {
        log.info("PATIENT: Delete service triggered");
        patientRepository.deleteById(id);
        log.info("PATIENT: Delete service is done");

    }

    public Optional<Patient> findPatientById(UUID id) {
        log.info("PATIENT: Find Patient -ID- service triggered");
        return patientRepository.findById(id);
    }

    public boolean findPatientByEmail(String email) {
        log.info("PATIENT: Find Patient -EMAIL- service triggered");
        return patientRepository.existsByEmail(email);
    }


}
