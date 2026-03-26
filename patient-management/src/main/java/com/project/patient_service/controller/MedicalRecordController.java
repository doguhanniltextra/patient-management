package com.project.patient_service.controller;

import com.project.patient_service.constants.Endpoints;
import com.project.patient_service.dto.CreateMedicalRecordRequestDto;
import com.project.patient_service.dto.MedicalRecordResponseDto;
import com.project.patient_service.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Endpoints.MEDICAL_RECORD_CONTROLLER_REQUEST)
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordResponseDto> createRecord(@Valid @RequestBody CreateMedicalRecordRequestDto request) {
        return ResponseEntity.ok(medicalRecordService.createMedicalRecord(request));
    }

    @GetMapping(Endpoints.MEDICAL_RECORD_CONTROLLER_GET_BY_PATIENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN') or @securityService.isPatientOwner(authentication, #patientId)")
    public ResponseEntity<List<MedicalRecordResponseDto>> getRecordsByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatient(patientId));
    }
}
