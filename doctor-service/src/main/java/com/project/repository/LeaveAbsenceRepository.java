package com.project.repository;

import com.project.model.LeaveAbsence;
import com.project.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LeaveAbsenceRepository extends JpaRepository<LeaveAbsence, UUID> {
    List<LeaveAbsence> findByDoctorIdAndStatus(UUID doctorId, LeaveStatus status);
    boolean existsByDoctorIdAndStatusAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
            UUID doctorId, LeaveStatus status, LocalDateTime endDateTime, LocalDateTime startDateTime
    );
}
