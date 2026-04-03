package com.project.notification_service.repository;

import com.project.notification_service.model.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, UUID> {
}
