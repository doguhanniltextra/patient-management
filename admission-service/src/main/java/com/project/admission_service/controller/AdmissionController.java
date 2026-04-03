package com.project.admission_service.controller;

import com.project.admission_service.constants.Endpoints;
import com.project.admission_service.dto.AdmissionRequest;
import com.project.admission_service.model.Admission;
import com.project.admission_service.service.AdmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Endpoints.ADMISSION_BASE)
public class AdmissionController {
    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @PostMapping(Endpoints.ADMIT)
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<Admission> admitPatient(@RequestBody AdmissionRequest request) {
        return ResponseEntity.ok(admissionService.admitPatient(request));
    }

    @PutMapping(Endpoints.DISCHARGE)
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<Admission> dischargePatient(@PathVariable UUID id) {
        return ResponseEntity.ok(admissionService.dischargePatient(id));
    }

    @GetMapping(Endpoints.LIST_ACTIVE)
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<List<Admission>> getActiveAdmissions() {
        return ResponseEntity.ok(admissionService.getActiveAdmissions());
    }
}
