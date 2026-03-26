package com.project.patient_service.service;

import com.project.patient_service.dto.CreateMedicalRecordRequestDto;
import com.project.patient_service.dto.MedicalRecordResponseDto;
import com.project.patient_service.model.MedicalRecord;
import com.project.patient_service.repository.MedicalRecordRepository;
import com.project.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, PatientRepository patientRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
    }

    public MedicalRecordResponseDto createMedicalRecord(CreateMedicalRecordRequestDto request) {
        if (!patientRepository.existsById(request.getPatientId())) {
            throw new RuntimeException("Patient not found: " + request.getPatientId());
        }

        MedicalRecord record = new MedicalRecord();
        record.setPatientId(request.getPatientId());
        record.setDoctorId(request.getDoctorId());
        record.setAppointmentId(request.getAppointmentId());
        record.setDiagnosis(request.getDiagnosis());
        record.setMedication(request.getMedication());
        record.setNotes(request.getNotes());
        record.setCreatedAt(LocalDateTime.now());

        MedicalRecord saved = medicalRecordRepository.save(record);
        return mapToDto(saved);
    }

    public List<MedicalRecordResponseDto> getRecordsByPatient(UUID patientId) {
        return medicalRecordRepository.findByPatientId(patientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private MedicalRecordResponseDto mapToDto(MedicalRecord record) {
        MedicalRecordResponseDto dto = new MedicalRecordResponseDto();
        dto.setId(record.getId());
        dto.setPatientId(record.getPatientId());
        dto.setDoctorId(record.getDoctorId());
        dto.setAppointmentId(record.getAppointmentId());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setMedication(record.getMedication());
        dto.setNotes(record.getNotes());
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }
}
