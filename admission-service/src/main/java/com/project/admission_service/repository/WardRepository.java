package com.project.admission_service.repository;

import com.project.admission_service.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WardRepository extends JpaRepository<Ward, UUID> {
}
