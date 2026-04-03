package com.project.admission_service.repository;

import com.project.admission_service.model.Bed;
import com.project.admission_service.model.BedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BedRepository extends JpaRepository<Bed, UUID> {
    List<Bed> findByRoomIdAndStatus(UUID roomId, BedStatus status);
}
