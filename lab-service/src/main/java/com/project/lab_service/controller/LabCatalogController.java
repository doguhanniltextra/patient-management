package com.project.lab_service.controller;

import com.project.lab_service.constants.Endpoints;
import com.project.lab_service.model.LabTestCatalog;
import com.project.lab_service.repository.LabTestCatalogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.LAB_CATALOG)
public class LabCatalogController {
    private final LabTestCatalogRepository catalogRepository;

    public LabCatalogController(LabTestCatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST','ADMIN','PATIENT')")
    public List<LabTestCatalog> getCatalog() {
        return catalogRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public LabTestCatalog create(@RequestBody LabTestCatalog catalog) {
        return catalogRepository.save(catalog);
    }

    @PutMapping("/{testCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public LabTestCatalog update(@PathVariable String testCode, @RequestBody LabTestCatalog catalog) {
        catalog.setTestCode(testCode);
        return catalogRepository.save(catalog);
    }
}
