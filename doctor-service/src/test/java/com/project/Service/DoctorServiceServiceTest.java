package com.project.Service;

import com.project.dto.UpdateDoctorServiceRequestDto;
import com.project.dto.UpdateDoctorServiceResponseDto;
import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.dto.response.CreateDoctorServiceResponseDto;
import com.project.exception.DoctorNotFoundException;
import com.project.exception.EmailIsNotUniqueException;
import com.project.helper.DoctorMapper;
import com.project.helper.DoctorValidator;
import com.project.model.Doctor;
import com.project.repository.DoctorRepository;
import com.project.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.project.model.Specialization.Dermatologist;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceServiceTest {

    @InjectMocks
    private DoctorService doctorService;


    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private DoctorValidator doctorValidator;

    @Mock
    private DoctorRepository doctorRepository;

    @Test
    public void DoctorService_createDoctor_ReturnsResponseDtoList() throws EmailIsNotUniqueException {
        CreateDoctorServiceRequestDto createDoctorServiceRequestDto = new CreateDoctorServiceRequestDto();
        createDoctorServiceRequestDto.setName("Doguhan");

        String mockEmail = "doctor.mock@hospital.com";

        Doctor doctor = new Doctor();
        doctor.setName("Dr. Arda Yılmaz");
        doctor.setEmail(mockEmail);
        doctor.setNumber("5551234567");
        doctor.setSpecialization(Dermatologist);
        doctor.setYearsOfExperience(12);
        doctor.setHospitalName("Merkez Hastanesi");
        doctor.setDepartment("Kardiyoloji");
        doctor.setLicenseNumber(123456);
        doctor.setAvailable(true);
        doctor.setPatientCount(0);

       when(doctorMapper.toEntity(createDoctorServiceRequestDto))
               .thenReturn(doctor);
       when(doctorRepository.save(doctor))
               .thenReturn(doctor);
        CreateDoctorServiceResponseDto createDoctorServiceResponseDto = doctorService.createDoctor(createDoctorServiceRequestDto);

        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    public void DoctorService_updateDoctor_ReturnsUpdateDoctorServiceResponseDto() throws DoctorNotFoundException {
        UUID mockId = UUID.randomUUID();
        UpdateDoctorServiceRequestDto updateDoctorServiceRequestDto = new UpdateDoctorServiceRequestDto();
        updateDoctorServiceRequestDto.setName("Doguhan");
        UpdateDoctorServiceResponseDto updateDoctorServiceResponseDto = new UpdateDoctorServiceResponseDto();
        updateDoctorServiceResponseDto.setNumber("Doguhan");

        String mockEmail = "doctor.mock@hospital.com";

        Doctor doctor = new Doctor();
        doctor.setName("Dr. Arda Yılmaz");
        doctor.setEmail(mockEmail);
        doctor.setNumber("5551234567");
        doctor.setSpecialization(Dermatologist);
        doctor.setYearsOfExperience(12);
        doctor.setHospitalName("Merkez Hastanesi");
        doctor.setDepartment("Kardiyoloji");
        doctor.setLicenseNumber(123456);
        doctor.setAvailable(true);
        doctor.setPatientCount(0);
        Optional<Doctor> optionalDoctor = Optional.of(doctor);

        when(doctorRepository.findById(mockId))
                .thenReturn(optionalDoctor);
        when(doctorMapper.getDoctorRequestDto(updateDoctorServiceRequestDto, optionalDoctor))
                .thenReturn(doctor);
        when(doctorMapper.getUpdateDoctorServiceResponseDto(doctor))
                .thenReturn(updateDoctorServiceResponseDto);

        UpdateDoctorServiceResponseDto result = doctorService.updateDoctor(mockId, updateDoctorServiceRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo("Doguhan");

        verify(doctorRepository, times(1)).findById(mockId);
        verify(doctorRepository, times(1)).save(doctor);

    }

    @Test
    public void DoctorService_updateDoctor_ThrowsNotFoundException() {
        // Arrange
        UUID fakeId = UUID.randomUUID();
        when(doctorRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> doctorService.updateDoctor(fakeId, new UpdateDoctorServiceRequestDto()))
                .isInstanceOf(DoctorNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    public void DoctorService_deleteDoctor_ReturnsVoid(){
        UUID userValidatorId = UUID.randomUUID();

        doctorRepository.deleteById(userValidatorId);

        verify(doctorRepository, times(1)).deleteById(userValidatorId);
    }

    @Test
    public void DoctorService_findDoctorById_ReturnsOptionalDoctor() {
        // --- 1. ARRANGE ---
        UUID mockId = UUID.randomUUID();
        String mockEmail = "doctor.mock@hospital.com";


        Doctor doctor = new Doctor();
        doctor.setName("Dr. Arda Yılmaz");
        doctor.setEmail(mockEmail);
        doctor.setNumber("5551234567");
        doctor.setSpecialization(Dermatologist);
        doctor.setYearsOfExperience(12);
        doctor.setHospitalName("Merkez Hastanesi");
        doctor.setDepartment("Kardiyoloji");
        doctor.setLicenseNumber(123456);
        doctor.setAvailable(true);
        doctor.setPatientCount(0);


        Optional<Doctor> optionalDoctor = Optional.of(doctor);

        when(doctorRepository.findById(mockId)).thenReturn(optionalDoctor);

        Optional<Doctor> result = doctorService.findDoctorById(mockId);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Dr. Arda Yılmaz");
        assertThat(result).isEqualTo(optionalDoctor);

        verify(doctorRepository, times(1)).findById(mockId);
    }
}
