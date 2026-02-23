package com.project.patient_service.Helper;


import com.project.patient_service.dto.request.CreatePatientServiceRequestDto;
import com.project.patient_service.dto.request.KafkaPatientRequestDto;
import com.project.patient_service.exception.EmailAlreadyExistsException;
import com.project.patient_service.exception.PatientNotFoundException;
import com.project.patient_service.helper.UserValidator;
import com.project.patient_service.model.Patient;
import com.project.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void checkEmailExists_WhenEmailAlreadyExists_ShouldThrowException() {
        // Arrange
        CreatePatientServiceRequestDto requestDto = new CreatePatientServiceRequestDto();
        requestDto.setEmail("exists@mail.com");
        when(patientRepository.existsByEmail("exists@mail.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userValidator.CheckEmailIsExistsOrNotForCreatePatient(requestDto, patientRepository))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void getPatientForCreatePatient_WithInvalidDate_ShouldThrowIllegalArgumentException() {
        // Arrange
        CreatePatientServiceRequestDto requestDto = new CreatePatientServiceRequestDto();
        requestDto.setDateOfBirth("01-01-1990"); // Yanlış format, ISO-8601 değil
        requestDto.setRegisteredDate("2023-01-01");

        // Act & Assert
        assertThatThrownBy(() -> userValidator.getPatientForCreatePatient(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid date format");
    }

    @Test
    void getPatientForUpdateMethod_WhenIdDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userValidator.getPatientForUpdateMethod(id, patientRepository))
                .isInstanceOf(PatientNotFoundException.class);
    }

    @Test
    void getPatientResponseEntity_WhenPresent_ReturnsOk() {
        // Arrange
        Patient patient = new Patient();
        patient.setName("Test");
        Optional<Patient> optionalPatient = Optional.of(patient);

        // Act
        ResponseEntity<Patient> response = userValidator.getPatientResponseEntity(optionalPatient);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(patient);
    }

    @Test
    void getPatientResponseEntity_WhenEmpty_ReturnsNotFound() {
        // Act
        ResponseEntity<Patient> response = userValidator.getPatientResponseEntity(Optional.empty());

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getStringObjectMap_ShouldCreateValidKafkaMap() {
        // Arrange
        KafkaPatientRequestDto dto = new KafkaPatientRequestDto();
        dto.setId(UUID.randomUUID());
        dto.setName("Kafka Patient");
        dto.setEmail("kafka@mail.com");

        // Act
        Map<String, Object> result = userValidator.getStringObjectMap(dto);

        // Assert
        assertThat(result)
                .containsEntry("name", "Kafka Patient")
                .containsEntry("eventType", "PATIENT_CREATED");
        assertThat(result.get("patientId")).isEqualTo(dto.getId().toString());
    }
}