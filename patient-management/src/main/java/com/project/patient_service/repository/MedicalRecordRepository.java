package com.project.patient_service.repository;

import com.project.patient_service.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {
    List<MedicalRecord> findByPatientId(UUID patientId);
    List<MedicalRecord> findByDoctorId(UUID doctorId);
}
