package com.project.patient_service.controller;

import com.project.patient_service.constants.Endpoints;
import com.project.patient_service.model.LabResult;
import com.project.patient_service.repository.LabResultRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Endpoints.PATIENT_CONTROLLER_REQUEST)
public class LabResultController {
    private final LabResultRepository labResultRepository;

    public LabResultController(LabResultRepository labResultRepository) {
        this.labResultRepository = labResultRepository;
    }

    @GetMapping(Endpoints.PATIENT_LAB_RESULTS)
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'RECEPTIONIST') or @securityService.isPatientOwner(authentication, #patientId)")
    public List<LabResult> getPatientLabResults(@PathVariable UUID patientId) {
        return labResultRepository.findByPatientId(patientId);
    }

    @GetMapping(Endpoints.PATIENT_LAB_RESULTS_BY_ORDER)
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'RECEPTIONIST') or @securityService.isPatientOwner(authentication, #patientId)")
    public List<LabResult> getPatientLabResultsByOrder(@PathVariable UUID patientId, @PathVariable UUID labOrderId) {
        return labResultRepository.findByPatientIdAndLabOrderId(patientId, labOrderId);
    }
}
