package com.project.repository;

import com.project.model.Shift;
import com.project.model.ShiftStatus;
import com.project.model.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findByDoctorIdAndShiftDateBetweenAndStatus(UUID doctorId, LocalDate from, LocalDate to, ShiftStatus status);
    List<Shift> findByDoctorIdAndShiftDateAndStatus(UUID doctorId, LocalDate date, ShiftStatus status);
    List<Shift> findByDoctorIdAndShiftDateAndShiftTypeAndStatus(UUID doctorId, LocalDate shiftDate, ShiftType shiftType, ShiftStatus status);
}
