package com.project.patient_service.controller;

import com.project.patient_service.dto.response.*;
import com.project.patient_service.dto.request.UpdatePatientServiceRequestDto;
import com.project.patient_service.dto.request.UpdatePatientControllerRequestDto;
import com.project.patient_service.dto.request.CreatePatientControllerRequestDto;
import com.project.patient_service.dto.request.CreatePatientServiceRequestDto;
import com.project.patient_service.helper.UserMapper;
import com.project.patient_service.helper.UserValidator;
import com.project.patient_service.model.Patient;
import com.project.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "API for managing Patients")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    public PatientController(PatientService patientService, UserMapper userMapper, UserValidator userValidator) {
        this.patientService = patientService;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
    }

    @GetMapping
    @Operation(summary = "Get All Patients")
    public ResponseEntity<List<GetPatientControllerResponseDto>> getPatients() {
        log.info("PATIENT: Get Patients Controller Triggered");
        List<GetPatientServiceResponseDto> patients = patientService.getPatients();

        List<GetPatientControllerResponseDto> result = userMapper.getGetPatientControllerResponseDtos(patients);

        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    @Operation(summary = "Create A Patient")
    public ResponseEntity<CreatePatientServiceResponseDto> createPatient(@Valid @RequestBody CreatePatientControllerRequestDto createPatientControllerRequestDto){
        log.info("PATIENT: Create Patient Controller Triggered");

        CreatePatientServiceRequestDto createPatientServiceRequestDto = userMapper.getCreatePatientServiceRequestDto(createPatientControllerRequestDto);
        CreatePatientServiceResponseDto createPatientServiceResponseDto = patientService.createPatient(createPatientServiceRequestDto);

        return ResponseEntity.ok().body(createPatientServiceResponseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update A Patient")
    public ResponseEntity<UpdatePatientControllerResponseDto> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody UpdatePatientControllerRequestDto updatePatientControllerRequestDto) {
        log.info("PATIENT: Update Patient Controller Triggered");

        UpdatePatientServiceRequestDto updatePatientServiceRequestDto = userMapper.getUpdatePatientServiceRequestDto(updatePatientControllerRequestDto);
        UpdatePatientServiceResponseDto updatePatient = patientService.updatePatient(id, updatePatientServiceRequestDto);
        UpdatePatientControllerResponseDto updatePatientControllerResponseDto = userMapper.getUpdatePatientControllerResponseDto(updatePatient);

        return ResponseEntity.ok().body(updatePatientControllerResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete A Patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        log.info("PATIENT: Delete Patient Controller Triggered");
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Find A Patient By Id")
    public ResponseEntity<Patient> findPatientById(@PathVariable UUID id) {
        log.info("PATIENT: Find Patient By Id Controller Triggered");
        Optional<Patient> currentId = patientService.findPatientById(id);
        return userValidator.getPatientResponseEntity(currentId);
    }

    @GetMapping("/find/email/{email}")
    @Operation(summary = "Find A Patient By Email")
    public ResponseEntity<Boolean> findPatientByEmail(@PathVariable String email) {
        log.info("PATIENT: Find Patient By Email Controller Triggered");
        boolean patientByEmail = userValidator.isPatientByEmail(email, patientService);
        return userValidator.getBooleanResponseEntity(patientByEmail);
    }
}
