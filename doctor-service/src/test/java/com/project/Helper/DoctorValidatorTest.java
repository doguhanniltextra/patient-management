package com.project.Helper;



import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.exception.EmailIsNotUniqueException;
import com.project.helper.DoctorValidator;
import com.project.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorValidatorTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorValidator doctorValidator;

    @Test
    void checkEmailIsUniqueOrNotForCreate_WhenEmailIsUnique_ShouldNotThrowException() {
        // Arrange
        CreateDoctorServiceRequestDto dto = new CreateDoctorServiceRequestDto();
        dto.setEmail("unique@hospital.com");

        when(doctorRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        // Act & Assert
        assertThatCode(() -> doctorValidator.checkEmailIsUniqueOrNotForCreate(dto, doctorRepository))
                .doesNotThrowAnyException();

        verify(doctorRepository, times(1)).existsByEmail(dto.getEmail());
    }

    @Test
    void checkEmailIsUniqueOrNotForCreate_WhenEmailExists_ShouldThrowEmailIsNotUniqueException() {
        // Arrange
        CreateDoctorServiceRequestDto dto = new CreateDoctorServiceRequestDto();
        dto.setEmail("exists@hospital.com");

        when(doctorRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> doctorValidator.checkEmailIsUniqueOrNotForCreate(dto, doctorRepository))
                .isInstanceOf(EmailIsNotUniqueException.class)
                .hasMessage("Email is not unique.");

        verify(doctorRepository, times(1)).existsByEmail(dto.getEmail());
    }
}
