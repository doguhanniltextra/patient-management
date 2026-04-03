package com.project.repository;

import com.project.model.DoctorOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface DoctorOutboxEventRepository extends JpaRepository<DoctorOutboxEvent, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from DoctorOutboxEvent e where e.status = 'PENDING' and e.nextRetryAt <= ?1 order by e.createdAt asc")
    List<DoctorOutboxEvent> claimPending(Instant now);
}
