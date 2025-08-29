package com.project.patient_service.service;

import com.project.patient_service.dto.response.CreatePatientServiceResponseDto;
import com.project.patient_service.dto.response.GetPatientServiceResponseDto;
import com.project.patient_service.dto.request.KafkaPatientRequestDto;
import com.project.patient_service.dto.response.UpdatePatientServiceResponseDto;
import com.project.patient_service.dto.request.CreatePatientServiceRequestDto;
import com.project.patient_service.dto.request.UpdatePatientServiceRequestDto;
import com.project.patient_service.exception.EmailAlreadyExistsException;

import com.project.patient_service.helper.UserMapper;
import com.project.patient_service.kafka.KafkaProducer;
import com.project.patient_service.model.Patient;
import com.project.patient_service.repository.PatientRepository;
import com.project.patient_service.helper.UserValidator;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final KafkaProducer kafkaProducer;

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    UserValidator userValidator = new UserValidator();
    UserMapper userMapper = new UserMapper();

    public PatientService(PatientRepository patientRepository, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public List<GetPatientServiceResponseDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<GetPatientServiceResponseDto> result = userMapper.getGetPatientServiceResponseDtos(patients);
        return result;
    }

    public CreatePatientServiceResponseDto createPatient(CreatePatientServiceRequestDto patientRequestDTO) throws EmailAlreadyExistsException {

        Patient patient = userValidator.getPatientForCreatePatient(patientRequestDTO);

        Patient newPatient = patientRepository.save(patient);
        log.info("PATIENT: Patient ID -> {}",
                newPatient.getId());

        try {
            KafkaPatientRequestDto kafkaDto = userMapper.getKafkaPatientRequestDto(newPatient);
            kafkaProducer.sendEvent(kafkaDto);
        } catch (KafkaException e) {
            log.warn("Event publishing failed for patient {}, but patient created successfully", newPatient.getId());
        }
        CreatePatientServiceResponseDto createPatientServiceResponseDto = userMapper.getCreatePatientServiceResponseDto(patient);
        return createPatientServiceResponseDto;
    }

    public UpdatePatientServiceResponseDto updatePatient(UUID id, UpdatePatientServiceRequestDto updatePatientServiceRequestDto) {
        Patient patient = userValidator.getPatientForUpdateMethod(id, patientRepository);

        userValidator.checkEmailIsExistsOrNotForUpdatePatient(id, updatePatientServiceRequestDto, patientRepository);
        log.info("PATIENT: Update service triggered");

        userMapper.getUpdatePatientRequestDto(updatePatientServiceRequestDto, patient);
        log.info("PATIENT: Patient ID -> {}" , patient.getId());

        Patient updatedPatient = patientRepository.save(patient);
        log.info("PATIENT: Update service is done");

        UpdatePatientServiceResponseDto updatePatientServiceResponseDto = userMapper.getUpdatePatientServiceResponseDto(updatedPatient);
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
