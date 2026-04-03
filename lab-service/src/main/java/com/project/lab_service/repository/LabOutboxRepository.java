package com.project.lab_service.repository;

import com.project.lab_service.model.LabOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabOutboxRepository extends JpaRepository<LabOutboxEvent, UUID> {
    List<LabOutboxEvent> findByStatus(String status);
}
