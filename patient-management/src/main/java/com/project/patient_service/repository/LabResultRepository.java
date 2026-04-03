package com.project.patient_service.repository;

import com.project.patient_service.model.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabResultRepository extends JpaRepository<LabResult, UUID> {
    List<LabResult> findByPatientId(UUID patientId);
    List<LabResult> findByPatientIdAndLabOrderId(UUID patientId, UUID labOrderId);
    Optional<LabResult> findByEventId(String eventId);
    Optional<LabResult> findByLabOrderIdAndTestCode(UUID labOrderId, String testCode);
}
