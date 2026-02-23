package com.project.patient_service.Helper;


import com.project.patient_service.dto.request.*;
import com.project.patient_service.dto.response.*;
import com.project.patient_service.helper.UserMapper;
import com.project.patient_service.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper(); // Spring'e gerek yok, direkt nesne oluşturuyoruz
    }

    @Test
    void getCreatePatientServiceResponseDto_ShouldMapCorrectly() {
        // Arrange
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setName("Ali Veli");
        patient.setEmail("ali@gmail.com");
        patient.setAddress("Istanbul");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));

        // Act
        CreatePatientServiceResponseDto response = userMapper.getCreatePatientServiceResponseDto(patient);

        // Assert
        assertThat(response.getId()).isEqualTo(patient.getId().toString());
        assertThat(response.getName()).isEqualTo(patient.getName());
        assertThat(response.getEmail()).isEqualTo(patient.getEmail());
    }

    @Test
    void getGetPatientServiceResponseDtos_ShouldMapListCorrectly() {
        // Arrange
        Patient p1 = new Patient();
        p1.setName("Patient 1");
        Patient p2 = new Patient();
        p2.setName("Patient 2");
        List<Patient> patients = List.of(p1, p2);

        // Act
        List<GetPatientServiceResponseDto> result = userMapper.getGetPatientServiceResponseDtos(patients);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Patient 1");
        assertThat(result.get(1).getName()).isEqualTo("Patient 2");
    }

    @Test
    void getUpdatePatientRequestDto_ShouldUpdateExistingPatientObject() {
        // Arrange
        Patient patient = new Patient();
        UpdatePatientServiceRequestDto requestDto = new UpdatePatientServiceRequestDto();
        requestDto.setName("Updated Name");
        requestDto.setAddress("Ankara");
        requestDto.setEmail("updated@gmail.com");
        requestDto.setDateOfBirth("1995-05-05");

        // Act
        userMapper.getUpdatePatientRequestDto(requestDto, patient);

        // Assert
        assertThat(patient.getName()).isEqualTo("Updated Name");
        assertThat(patient.getEmail()).isEqualTo("updated@gmail.com");
        assertThat(patient.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 5, 5));
    }

    @Test
    void getCreatePatientServiceRequestDto_ShouldMapControllerToService() {
        // Arrange
        CreatePatientControllerRequestDto controllerRequest = new CreatePatientControllerRequestDto();
        controllerRequest.setName("Test Name");
        controllerRequest.setEmail("test@gmail.com");

        // Act
        CreatePatientServiceRequestDto serviceRequest = userMapper.getCreatePatientServiceRequestDto(controllerRequest);

        // Assert
        assertThat(serviceRequest.getName()).isEqualTo(controllerRequest.getName());
        assertThat(serviceRequest.getEmail()).isEqualTo(controllerRequest.getEmail());
    }
}