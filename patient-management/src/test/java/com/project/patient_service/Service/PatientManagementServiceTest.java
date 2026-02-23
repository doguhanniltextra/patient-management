package com.project.patient_service.Service;

import com.project.patient_service.dto.request.CreatePatientServiceRequestDto;
import com.project.patient_service.dto.request.KafkaPatientRequestDto;
import com.project.patient_service.dto.request.UpdatePatientServiceRequestDto;
import com.project.patient_service.dto.response.CreatePatientServiceResponseDto;
import com.project.patient_service.dto.response.GetPatientServiceResponseDto;
import com.project.patient_service.dto.response.UpdatePatientServiceResponseDto;
import com.project.patient_service.helper.UserMapper;
import com.project.patient_service.helper.UserValidator;
import com.project.patient_service.model.Patient;
import com.project.patient_service.repository.PatientRepository;
import com.project.patient_service.service.PatientService;
import com.project.patient_service.exception.EmailAlreadyExistsException; // Paket yolunu kontrol et
import com.project.patient_service.kafka.KafkaProducer; // Paket yolunu kontrol et
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientManagementServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private KafkaProducer kafkaProducer; // Metodunda kullanılan producer nesnesi

    @InjectMocks
    private PatientService patientService;

    @Test
    public void PatientService_GetPatients_ReturnsResponseDtoList() {
        // --- ARRANGE ---
        Patient patient = new Patient();
        patient.setName("Test Patient");
        patient.setEmail("test@gmail.com");
        patient.setAddress("Istanbul, Turkey");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setRegisteredDate(LocalDate.now());

        List<Patient> mockPatientList = List.of(patient);

        GetPatientServiceResponseDto responseDto = new GetPatientServiceResponseDto();
        responseDto.setEmail("test@gmail.com");
        List<GetPatientServiceResponseDto> mockDtoList = List.of(responseDto);

        // --- STUBBING ---
        when(patientRepository.findAll()).thenReturn(mockPatientList);
        when(userMapper.getGetPatientServiceResponseDtos(anyList())).thenReturn(mockDtoList);

        // --- ACT ---
        List<GetPatientServiceResponseDto> result = patientService.getPatients();

        // --- ASSERT ---
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    public void PatientService_CreatePatient_ReturnsResponse() throws EmailAlreadyExistsException {
        // --- 1. DATA PREPARATION ---
        CreatePatientServiceRequestDto requestDto = new CreatePatientServiceRequestDto();
        requestDto.setEmail("test@gmail.com");
        requestDto.setName("Test Patient");

        requestDto.setDateOfBirth("1990-01-01");
        requestDto.setAddress("Istanbul");
        requestDto.setRegisteredDate(String.valueOf(LocalDate.now()));

        Patient mockPatient = new Patient();
        mockPatient.setId(UUID.randomUUID());

        String testEmail = "test_email@gmail.com";
        mockPatient.setName("Test Patient");
        mockPatient.setEmail(testEmail);
        mockPatient.setAddress("Istanbul, Turkey");
        mockPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        mockPatient.setRegisteredDate(LocalDate.now());

        CreatePatientServiceResponseDto mockResponse = new CreatePatientServiceResponseDto();
        mockResponse.setEmail("test@gmail.com");

        // --- 2. STUBBING ---
        lenient().when(userValidator.getPatientForCreatePatient(any(CreatePatientServiceRequestDto.class)))
                .thenReturn(mockPatient);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(mockPatient);

        when(userMapper.getKafkaPatientRequestDto(any(Patient.class)))
                .thenReturn(new KafkaPatientRequestDto());

        when(userMapper.getCreatePatientServiceResponseDto(any(Patient.class)))
                .thenReturn(mockResponse);

        // --- 3. ACT ---
        CreatePatientServiceResponseDto result = patientService.createPatient(requestDto);

        // --- 4. ASSERT ---
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");


        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(kafkaProducer, times(1)).sendEvent(any(KafkaPatientRequestDto.class));
        verify(userMapper, times(1)).getCreatePatientServiceResponseDto(any(Patient.class));
    }

    @Test
    public void PatientService_UpdatePatient_ReturnsResponse() {
        UUID userValidatorId = UUID.randomUUID();
        String testEmail = "test_email@gmail.com";

        Patient mockPatient = new Patient();
        mockPatient.setName("Test Patient");
        mockPatient.setEmail(testEmail);
        mockPatient.setAddress("Istanbul, Turkey");
        mockPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        mockPatient.setRegisteredDate(LocalDate.now());

        UpdatePatientServiceRequestDto updateRequest = new UpdatePatientServiceRequestDto();
        updateRequest.setEmail("yeni@gmail.com");
        updateRequest.setName("Yeni İsim");
        updateRequest.setAddress("yeni mail");
        updateRequest.setDateOfBirth(String.valueOf(LocalDate.of(1990, 1, 1)));

        UpdatePatientServiceResponseDto mockResponse = new UpdatePatientServiceResponseDto();
        mockResponse.setEmail("yeni@gmail.com");


        doReturn(mockPatient).when(userValidator).getPatientForUpdateMethod(any(), any());
        doReturn(mockResponse).when(userMapper).getUpdatePatientServiceResponseDto(any());


        UpdatePatientServiceResponseDto result = patientService.updatePatient(userValidatorId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("yeni@gmail.com");


        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    public void PatientService_DeletePatient_ReturnsVoid(){
        UUID userValidatorId = UUID.randomUUID();

        patientService.deletePatient(userValidatorId);

        verify(patientRepository, times(1)).deleteById(userValidatorId);
    }

    @Test
    public void PatientService_FindPatientById_ReturnsPatient() {
        UUID userValidatorId = UUID.randomUUID();

        Optional<Patient> patient = patientService.findPatientById(userValidatorId);

        assertThat(patient).isNotNull();

        verify(patientRepository, times(1))
                .findById(userValidatorId);
    }

    @Test
    public void PatientService_FindPatientByEmail_ReturnsFalse(){
        String email = "test@gmail.com";

        boolean bool = patientService.findPatientByEmail(email);

        assertThat(bool).isFalse();

        verify(patientRepository, times(1))
                .existsByEmail(email);
    }

    @Test
    public void PatientService_FindPatientByEmail_ReturnsTrue(){
        // --- 1. ARRANGE ---
        String testEmail = "test_email@gmail.com";

        when(patientRepository.existsByEmail(testEmail)).thenReturn(true);

        // --- 2. ACT ---
        boolean result = patientService.findPatientByEmail(testEmail);

        // --- 3. ASSERT ---
        assertThat(result).isTrue();

        // --- 4. VERIFY ---
        verify(patientRepository, times(1)).existsByEmail(testEmail);
    }

}