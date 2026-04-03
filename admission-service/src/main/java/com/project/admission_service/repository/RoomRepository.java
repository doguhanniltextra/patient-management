package com.project.admission_service.repository;

import com.project.admission_service.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByWardId(UUID wardId);
}
