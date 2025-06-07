package com.project.controller;

import com.project.exception.EmailIsNotUniqueException;
import com.project.exception.IdIsValidException.IdIsValidException;
import com.project.model.Doctor;
import com.project.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/doctors")
@Tag(name = "Doctor", description = "API for managing Doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    @Operation(summary = "Get All Doctors")
    public ResponseEntity<List<Doctor>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok().body(doctors);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update A Doctor")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable UUID id, @Validated({Default.class}) @RequestBody Doctor doctor) {
        Doctor updated_doctor = doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok().body(updated_doctor);
    }

    @PostMapping
    @Operation(summary = "Create A Doctor")
    public ResponseEntity<Doctor> createDoctor(@Valid @RequestBody Doctor doctor) throws IdIsValidException, EmailIsNotUniqueException {

        Doctor createdDoctor = doctorService.createDoctor(doctor);
        return ResponseEntity.ok().body(createdDoctor);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete A Doctor")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Doctor> findPatientById(@PathVariable UUID id) {
        Optional<Doctor> currentId = doctorService.findDoctorById(id);
        if (currentId.isPresent()) {
            return ResponseEntity.ok(currentId.get()); // 200 OK + body
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

}
