package com.project.patient_service.Service;

import com.project.patient_service.dto.CreateMedicalRecordRequestDto;
import com.project.patient_service.dto.MedicalRecordResponseDto;
import com.project.patient_service.model.MedicalRecord;
import com.project.patient_service.repository.MedicalRecordRepository;
import com.project.patient_service.repository.PatientRepository;
import com.project.patient_service.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @Test
    public void createMedicalRecord_Success() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();

        CreateMedicalRecordRequestDto request = new CreateMedicalRecordRequestDto();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setAppointmentId(appointmentId);
        request.setDiagnosis("Flu");
        request.setMedication("Paracetamol");

        when(patientRepository.existsById(patientId)).thenReturn(true);

        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(invocation -> {
            MedicalRecord record = invocation.getArgument(0);
            record.setId(UUID.randomUUID());
            record.setCreatedAt(LocalDateTime.now());
            return record;
        });

        MedicalRecordResponseDto response = medicalRecordService.createMedicalRecord(request);

        assertThat(response).isNotNull();
        assertThat(response.getPatientId()).isEqualTo(patientId);
        assertThat(response.getDiagnosis()).isEqualTo("Flu");
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    public void createMedicalRecord_PatientNotFound_ThrowsException() {
        UUID patientId = UUID.randomUUID();
        CreateMedicalRecordRequestDto request = new CreateMedicalRecordRequestDto();
        request.setPatientId(patientId);

        when(patientRepository.existsById(patientId)).thenReturn(false);

        assertThatThrownBy(() -> medicalRecordService.createMedicalRecord(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Patient not found");

        verify(medicalRecordRepository, never()).save(any(MedicalRecord.class));
    }

    @Test
    public void getRecordsByPatient_Success() {
        UUID patientId = UUID.randomUUID();

        MedicalRecord record1 = new MedicalRecord();
        record1.setId(UUID.randomUUID());
        record1.setPatientId(patientId);
        record1.setDiagnosis("Migraine");

        MedicalRecord record2 = new MedicalRecord();
        record2.setId(UUID.randomUUID());
        record2.setPatientId(patientId);
        record2.setDiagnosis("Toothache");

        when(medicalRecordRepository.findByPatientId(patientId)).thenReturn(List.of(record1, record2));

        List<MedicalRecordResponseDto> response = medicalRecordService.getRecordsByPatient(patientId);

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getPatientId()).isEqualTo(patientId);
        assertThat(response.get(0).getDiagnosis()).isEqualTo("Migraine");
        assertThat(response.get(1).getDiagnosis()).isEqualTo("Toothache");

        verify(medicalRecordRepository, times(1)).findByPatientId(patientId);
    }
}
