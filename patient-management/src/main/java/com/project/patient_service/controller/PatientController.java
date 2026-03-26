package com.project.patient_service.controller;

import com.project.patient_service.constants.Endpoints;
import com.project.patient_service.constants.LogMessages;
import com.project.patient_service.constants.SwaggerMessages;
import com.project.patient_service.dto.response.GetPatientControllerResponseDto;
import com.project.patient_service.dto.response.GetPatientServiceResponseDto;
import com.project.patient_service.dto.response.UpdatePatientControllerResponseDto;
import com.project.patient_service.dto.response.UpdatePatientServiceResponseDto;
import com.project.patient_service.dto.request.UpdatePatientServiceRequestDto;
import com.project.patient_service.dto.request.UpdatePatientControllerRequestDto;
import com.project.patient_service.dto.request.CreatePatientControllerRequestDto;
import com.project.patient_service.dto.response.CreatePatientServiceResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(Endpoints.PATIENT_CONTROLLER_REQUEST)
@Tag(name = SwaggerMessages.PATIENT_CONTROLLER_NAME, description = SwaggerMessages.PATIENT_CONTROLLER_DESCRIPTION)
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
    @Operation(summary = SwaggerMessages.GET_PATIENTS)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Page<GetPatientControllerResponseDto>> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info(LogMessages.CONTROLLER_GET_TRIGGERED);

        // Cap page size to prevent abuse
        int safeSize = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, safeSize);

        Page<GetPatientServiceResponseDto> patients = patientService.getPatients(pageable);
        Page<GetPatientControllerResponseDto> result = patients.map(userMapper::toControllerResponseDto);

        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    @Operation(summary = SwaggerMessages.CREATE_PATIENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<CreatePatientServiceResponseDto> createPatient(@Valid @RequestBody CreatePatientControllerRequestDto createPatientControllerRequestDto){
        log.info(LogMessages.CONTROLLER_CREATE_TRIGGERED);

        CreatePatientServiceRequestDto createPatientServiceRequestDto = userMapper.getCreatePatientServiceRequestDto(createPatientControllerRequestDto);
        CreatePatientServiceResponseDto createPatientServiceResponseDto = patientService.createPatient(createPatientServiceRequestDto);

        return ResponseEntity.ok().body(createPatientServiceResponseDto);
    }

    @PutMapping(Endpoints.PATIENT_CONTROLLER_UPDATE_PATIENT)
    @Operation(summary = SwaggerMessages.UPDATE_PATIENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @securityService.isPatientOwner(authentication, #id)")
    public ResponseEntity<UpdatePatientControllerResponseDto> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody UpdatePatientControllerRequestDto updatePatientControllerRequestDto) {
        log.info(LogMessages.CONTROLLER_UPDATE_TRIGGERED);

        UpdatePatientServiceRequestDto updatePatientServiceRequestDto = userMapper.getUpdatePatientServiceRequestDto(updatePatientControllerRequestDto);
        UpdatePatientServiceResponseDto updatePatient = patientService.updatePatient(id, updatePatientServiceRequestDto);
        UpdatePatientControllerResponseDto updatePatientControllerResponseDto = userMapper.getUpdatePatientControllerResponseDto(updatePatient);

        return ResponseEntity.ok().body(updatePatientControllerResponseDto);
    }

    @DeleteMapping(Endpoints.PATIENT_CONTROLLER_DELETE_PATIENT)
    @Operation(summary = SwaggerMessages.DELETE_PATIENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        log.info(LogMessages.CONTROLLER_DELETE_TRIGGERED);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Endpoints.PATIENT_CONTROLLER_FIND_PATIENT_BY_ID)
    @Operation(summary = SwaggerMessages.FIND_PATIENT_BY_ID)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'RECEPTIONIST') or @securityService.isPatientOwner(authentication, #id)")
    public ResponseEntity<Patient> findPatientById(@PathVariable UUID id) {
        log.info(LogMessages.CONTROLLER_FIND_BY_ID_TRIGGERED);
        Optional<Patient> currentId = patientService.findPatientById(id);
        return userValidator.getPatientResponseEntity(currentId);
    }

    @GetMapping(Endpoints.PATIENT_CONTROLLER_FIND_PATIENT_BY_EMAIL)
    @Operation(summary = SwaggerMessages.FIND_PATIENT_BY_EMAIL)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Boolean> findPatientByEmail(@PathVariable String email) {
        log.info(LogMessages.CONTROLLER_FIND_BY_EMAIL_TRIGGERED);
        boolean patientByEmail = userValidator.isPatientByEmail(email, patientService);
        return userValidator.getBooleanResponseEntity(patientByEmail);
    }
}
