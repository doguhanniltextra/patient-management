package com.project.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
}
