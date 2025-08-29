package com.project.patient_service.helper;

import com.project.patient_service.dto.request.*;
import com.project.patient_service.dto.response.*;
import com.project.patient_service.model.Patient;
import com.project.patient_service.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class UserMapper {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    public CreatePatientServiceResponseDto getCreatePatientServiceResponseDto(Patient patient) {
        CreatePatientServiceResponseDto createPatientServiceResponseDto = new CreatePatientServiceResponseDto();
        createPatientServiceResponseDto.setId(String.valueOf(patient.getId()));
        createPatientServiceResponseDto.setName(patient.getName());
        createPatientServiceResponseDto.setEmail(patient.getEmail());
        createPatientServiceResponseDto.setAddress(patient.getAddress());
        createPatientServiceResponseDto.setDateOfBirth(patient.getDateOfBirth());
        return createPatientServiceResponseDto;
    }
    public KafkaPatientRequestDto getKafkaPatientRequestDto(Patient newPatient) {
        KafkaPatientRequestDto kafkaPatientRequestDto = new KafkaPatientRequestDto();
        kafkaPatientRequestDto.setId(newPatient.getId());
        kafkaPatientRequestDto.setEmail(newPatient.getEmail());
        kafkaPatientRequestDto.setName(newPatient.getName());
        return kafkaPatientRequestDto;
    }
    public static UpdatePatientServiceResponseDto getUpdatePatientServiceResponseDto(Patient updatedPatient) {
        UpdatePatientServiceResponseDto updatePatientServiceResponseDto = new UpdatePatientServiceResponseDto();
        updatePatientServiceResponseDto.setName(updatedPatient.getName());
        updatePatientServiceResponseDto.setAddress(updatedPatient.getAddress());
        updatePatientServiceResponseDto.setDateOfBirth(updatedPatient.getDateOfBirth());
        updatePatientServiceResponseDto.setEmail(updatedPatient.getEmail());
        return updatePatientServiceResponseDto;
    }
    public static void getUpdatePatientRequestDto(UpdatePatientServiceRequestDto updatePatientServiceRequestDto, Patient patient) {
        patient.setName(updatePatientServiceRequestDto.getName());
        patient.setAddress(updatePatientServiceRequestDto.getAddress());
        patient.setEmail(updatePatientServiceRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(updatePatientServiceRequestDto.getDateOfBirth()));
    }
    public static List<GetPatientServiceResponseDto> getGetPatientServiceResponseDtos(List<Patient> patients) {
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
    public static CreatePatientServiceRequestDto getCreatePatientServiceRequestDto(CreatePatientControllerRequestDto createPatientControllerRequestDto) {
        CreatePatientServiceRequestDto createPatientServiceRequestDto = new CreatePatientServiceRequestDto();
        createPatientServiceRequestDto.setAddress(createPatientControllerRequestDto.getAddress());
        createPatientServiceRequestDto.setEmail(createPatientControllerRequestDto.getEmail());
        createPatientServiceRequestDto.setName(createPatientControllerRequestDto.getName());
        createPatientServiceRequestDto.setRegisteredDate(createPatientControllerRequestDto.getRegisteredDate());
        createPatientServiceRequestDto.setDateOfBirth(createPatientControllerRequestDto.getDateOfBirth());
        return createPatientServiceRequestDto;
    }
    public static List<GetPatientControllerResponseDto> getGetPatientControllerResponseDtos(List<GetPatientServiceResponseDto> patients) {
        List<GetPatientControllerResponseDto> result = patients
                .stream()
                .map(patient -> {
                    GetPatientControllerResponseDto getPatientControllerResponseDto1 = new GetPatientControllerResponseDto();
                    getPatientControllerResponseDto1.setAddress(patient.getAddress());
                    getPatientControllerResponseDto1.setId(String.valueOf(patient.getId()));
                    getPatientControllerResponseDto1.setName(patient.getName());
                    getPatientControllerResponseDto1.setEmail(patient.getEmail());
                    getPatientControllerResponseDto1.setDateOfBirth(patient.getDateOfBirth());
                    log.info("PATIENT: Get Patients Controller -MAPPING- is done");
                    return getPatientControllerResponseDto1;
                }).toList();
        return result;
    }

    public static UpdatePatientControllerResponseDto getUpdatePatientControllerResponseDto(UpdatePatientServiceResponseDto updatePatient) {
        UpdatePatientControllerResponseDto updatePatientControllerResponseDto = new UpdatePatientControllerResponseDto();
        updatePatientControllerResponseDto.setName(updatePatient.getName());
        updatePatientControllerResponseDto.setAddress(updatePatient.getAddress());
        updatePatientControllerResponseDto.setDateOfBirth(updatePatient.getDateOfBirth());
        updatePatientControllerResponseDto.setEmail(updatePatient.getEmail());
        return updatePatientControllerResponseDto;
    }

    public static UpdatePatientServiceRequestDto getUpdatePatientServiceRequestDto(UpdatePatientControllerRequestDto updatePatientControllerRequestDto) {
        UpdatePatientServiceRequestDto updatePatientServiceRequestDto = new UpdatePatientServiceRequestDto();
        updatePatientServiceRequestDto.setName(updatePatientControllerRequestDto.getName());
        updatePatientServiceRequestDto.setAddress(updatePatientControllerRequestDto.getAddress());
        updatePatientServiceRequestDto.setDateOfBirth(updatePatientControllerRequestDto.getDateOfBirth());
        updatePatientServiceRequestDto.setEmail(updatePatientControllerRequestDto.getEmail());
        return updatePatientServiceRequestDto;
    }
}
