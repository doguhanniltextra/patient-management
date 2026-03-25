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
    public  UpdatePatientServiceResponseDto getUpdatePatientServiceResponseDto(Patient updatedPatient) {
        UpdatePatientServiceResponseDto updatePatientServiceResponseDto = new UpdatePatientServiceResponseDto();
        updatePatientServiceResponseDto.setName(updatedPatient.getName());
        updatePatientServiceResponseDto.setAddress(updatedPatient.getAddress());
        updatePatientServiceResponseDto.setDateOfBirth(updatedPatient.getDateOfBirth());
        updatePatientServiceResponseDto.setEmail(updatedPatient.getEmail());
        return updatePatientServiceResponseDto;
    }
    public  void getUpdatePatientRequestDto(UpdatePatientServiceRequestDto updatePatientServiceRequestDto, Patient patient) {
        patient.setName(updatePatientServiceRequestDto.getName());
        patient.setAddress(updatePatientServiceRequestDto.getAddress());
        patient.setEmail(updatePatientServiceRequestDto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(updatePatientServiceRequestDto.getDateOfBirth()));
    }
    // Single-object mapper for Page.map() — used by paginated endpoints
    public GetPatientServiceResponseDto toServiceResponseDto(Patient patient) {
        GetPatientServiceResponseDto dto = new GetPatientServiceResponseDto();
        dto.setId(patient.getId());
        dto.setEmail(patient.getEmail());
        dto.setAddress(patient.getAddress());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setName(patient.getName());
        return dto;
    }

    // Single-object mapper for Page.map() — used by paginated endpoints
    public GetPatientControllerResponseDto toControllerResponseDto(GetPatientServiceResponseDto patient) {
        GetPatientControllerResponseDto dto = new GetPatientControllerResponseDto();
        dto.setAddress(patient.getAddress());
        dto.setId(String.valueOf(patient.getId()));
        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setDateOfBirth(patient.getDateOfBirth());
        return dto;
    }

    public  List<GetPatientServiceResponseDto> getGetPatientServiceResponseDtos(List<Patient> patients) {
        List<GetPatientServiceResponseDto> result = patients
                .stream()
                .map(this::toServiceResponseDto).toList();
        return result;
    }
    public  CreatePatientServiceRequestDto getCreatePatientServiceRequestDto(CreatePatientControllerRequestDto createPatientControllerRequestDto) {
        CreatePatientServiceRequestDto createPatientServiceRequestDto = new CreatePatientServiceRequestDto();
        createPatientServiceRequestDto.setAddress(createPatientControllerRequestDto.getAddress());
        createPatientServiceRequestDto.setEmail(createPatientControllerRequestDto.getEmail());
        createPatientServiceRequestDto.setName(createPatientControllerRequestDto.getName());
        createPatientServiceRequestDto.setRegisteredDate(createPatientControllerRequestDto.getRegisteredDate());
        createPatientServiceRequestDto.setDateOfBirth(createPatientControllerRequestDto.getDateOfBirth());
        return createPatientServiceRequestDto;
    }
    public  List<GetPatientControllerResponseDto> getGetPatientControllerResponseDtos(List<GetPatientServiceResponseDto> patients) {
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

    public  UpdatePatientControllerResponseDto getUpdatePatientControllerResponseDto(UpdatePatientServiceResponseDto updatePatient) {
        UpdatePatientControllerResponseDto updatePatientControllerResponseDto = new UpdatePatientControllerResponseDto();
        updatePatientControllerResponseDto.setName(updatePatient.getName());
        updatePatientControllerResponseDto.setAddress(updatePatient.getAddress());
        updatePatientControllerResponseDto.setDateOfBirth(updatePatient.getDateOfBirth());
        updatePatientControllerResponseDto.setEmail(updatePatient.getEmail());
        return updatePatientControllerResponseDto;
    }

    public  UpdatePatientServiceRequestDto getUpdatePatientServiceRequestDto(UpdatePatientControllerRequestDto updatePatientControllerRequestDto) {
        UpdatePatientServiceRequestDto updatePatientServiceRequestDto = new UpdatePatientServiceRequestDto();
        updatePatientServiceRequestDto.setName(updatePatientControllerRequestDto.getName());
        updatePatientServiceRequestDto.setAddress(updatePatientControllerRequestDto.getAddress());
        updatePatientServiceRequestDto.setDateOfBirth(updatePatientControllerRequestDto.getDateOfBirth());
        updatePatientServiceRequestDto.setEmail(updatePatientControllerRequestDto.getEmail());
        return updatePatientServiceRequestDto;
    }
}
