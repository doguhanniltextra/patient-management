package com.project.repository;

import com.project.model.DoctorNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DoctorNotificationRepository extends JpaRepository<DoctorNotification, UUID> {
    Optional<DoctorNotification> findByEventId(String eventId);
}
