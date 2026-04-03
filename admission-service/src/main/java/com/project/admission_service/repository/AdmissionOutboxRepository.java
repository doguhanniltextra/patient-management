package com.project.admission_service.repository;

import com.project.admission_service.model.AdmissionOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AdmissionOutboxRepository extends JpaRepository<AdmissionOutboxEvent, UUID> {
    List<AdmissionOutboxEvent> findByStatus(String status);
}
