package com.project.lab_service.repository;

import com.project.lab_service.model.LabTestCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabTestCatalogRepository extends JpaRepository<LabTestCatalog, String> {
}
