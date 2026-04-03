package com.project.admission_service.repository;

import com.project.admission_service.model.Admission;
import com.project.admission_service.model.AdmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AdmissionRepository extends JpaRepository<Admission, UUID> {
    List<Admission> findByStatus(AdmissionStatus status);
}
