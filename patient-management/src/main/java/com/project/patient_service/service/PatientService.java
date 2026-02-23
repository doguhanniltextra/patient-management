package com.project.patient_service.service;

import com.project.patient_service.constants.LogMessages;
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
    private final UserMapper userMapper;



    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final UserValidator userValidator;

    public PatientService(PatientRepository patientRepository, KafkaProducer kafkaProducer, UserMapper userMapper, UserValidator userValidator) {
        this.patientRepository = patientRepository;
        this.kafkaProducer = kafkaProducer;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }



    public List<GetPatientServiceResponseDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<GetPatientServiceResponseDto> result = userMapper.getGetPatientServiceResponseDtos(patients);
        return result;
    }

    public CreatePatientServiceResponseDto createPatient(CreatePatientServiceRequestDto patientRequestDTO) throws EmailAlreadyExistsException {

        Patient patient = userValidator.getPatientForCreatePatient(patientRequestDTO);

        Patient newPatient = patientRepository.save(patient);
        log.info(LogMessages.SERVICE_CREATE_TRIGGERED, newPatient.getId());

        try {
            KafkaPatientRequestDto kafkaDto = userMapper.getKafkaPatientRequestDto(newPatient);
            kafkaProducer.sendEvent(kafkaDto);
        } catch (KafkaException e) {
            log.warn(LogMessages.SERVICE_CREATE_KAFKA_ERROR, newPatient.getId());
        }
        CreatePatientServiceResponseDto createPatientServiceResponseDto = userMapper.getCreatePatientServiceResponseDto(patient);
        return createPatientServiceResponseDto;
    }

    public UpdatePatientServiceResponseDto updatePatient(UUID id, UpdatePatientServiceRequestDto updatePatientServiceRequestDto) {
        Patient patient = userValidator.getPatientForUpdateMethod(id, patientRepository);

        userValidator.checkEmailIsExistsOrNotForUpdatePatient(id, updatePatientServiceRequestDto, patientRepository);
        log.info(LogMessages.SERVICE_UPDATE_TRIGGERED);

        userMapper.getUpdatePatientRequestDto(updatePatientServiceRequestDto, patient);

        Patient updatedPatient = patientRepository.save(patient);

        UpdatePatientServiceResponseDto updatePatientServiceResponseDto = userMapper.getUpdatePatientServiceResponseDto(updatedPatient);
        return updatePatientServiceResponseDto;
    }

    public void deletePatient(UUID id) {
        log.info(LogMessages.SERVICE_DELETE_TRIGGERED);
        patientRepository.deleteById(id);
    }

    public Optional<Patient> findPatientById(UUID id) {
        log.info(LogMessages.SERVICE_FIND_BY_ID_TRIGGERED);
        return patientRepository.findById(id);
    }

    public boolean findPatientByEmail(String email) {
        log.info(LogMessages.SERVICE_FIND_BY_EMAIL_TRIGGERED);
        return patientRepository.existsByEmail(email);
    }
}
