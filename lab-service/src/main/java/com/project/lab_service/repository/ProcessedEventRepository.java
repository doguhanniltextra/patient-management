package com.project.lab_service.repository;

import com.project.lab_service.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
    Optional<ProcessedEvent> findByEventIdAndConsumerName(String eventId, String consumerName);
}
