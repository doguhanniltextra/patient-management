package com.project.repository;

import com.project.model.DoctorLabOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorLabOrderRepository extends JpaRepository<DoctorLabOrder, UUID> {
}
