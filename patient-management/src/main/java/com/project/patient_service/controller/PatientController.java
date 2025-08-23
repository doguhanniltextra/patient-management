package com.project.patient_service.controller;

import com.project.patient_service.dto.response.GetPatientControllerResponseDto;
import com.project.patient_service.dto.response.GetPatientServiceResponseDto;
import com.project.patient_service.dto.response.UpdatePatientServiceResponseDto;
import com.project.patient_service.dto.request.UpdatePatientServiceRequestDto;
import com.project.patient_service.dto.request.UpdatePatientControllerRequestDto;
import com.project.patient_service.dto.request.CreatePatientControllerRequestDto;
import com.project.patient_service.dto.request.CreatePatientServiceRequestDto;
import com.project.patient_service.dto.response.UpdatePatientControllerResponseDto;
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

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }


    @GetMapping
    @Operation(summary = "Get All Patients")
    public ResponseEntity<List<GetPatientControllerResponseDto>> getPatients() {
        log.info("PATIENT: Get Patients Controller Triggered");
        List<GetPatientServiceResponseDto> patients = patientService.getPatients();

        List<GetPatientControllerResponseDto> result = patients
                .stream()
                .map(patient -> {
                    GetPatientControllerResponseDto getPatientControllerResponseDto1 = new GetPatientControllerResponseDto();
                    getPatientControllerResponseDto1.setAddress(patient.getAddress());
                    getPatientControllerResponseDto1.setId(patient.getId());
                    getPatientControllerResponseDto1.setName(patient.getName());
                    getPatientControllerResponseDto1.setEmail(patient.getEmail());
                    getPatientControllerResponseDto1.setDateOfBirth(patient.getDateOfBirth());
                    log.info("PATIENT: Get Patients Controller -MAPPING- is done");
                    return getPatientControllerResponseDto1;
                }).toList();


        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    @Operation(summary = "Create A Patient")
    public ResponseEntity<UpdatePatientServiceResponseDto> createPatient(@Valid @RequestBody CreatePatientControllerRequestDto createPatientControllerRequestDto){
        log.info("PATIENT: Create Patient Controller Triggered");

        CreatePatientServiceRequestDto createPatientServiceRequestDto = new CreatePatientServiceRequestDto();
        createPatientServiceRequestDto.setAddress(createPatientControllerRequestDto.getAddress());
        createPatientServiceRequestDto.setEmail(createPatientControllerRequestDto.getEmail());
        createPatientServiceRequestDto.setName(createPatientControllerRequestDto.getName());
        createPatientServiceRequestDto.setRegisteredDate(createPatientControllerRequestDto.getRegisteredDate());
        createPatientServiceRequestDto.setDateOfBirth(createPatientControllerRequestDto.getDateOfBirth());


        UpdatePatientServiceResponseDto updatePatientServiceResponseDto = patientService.createPatient(createPatientServiceRequestDto);

        return ResponseEntity.ok().body(updatePatientServiceResponseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update A Patient")
    public ResponseEntity<UpdatePatientControllerResponseDto> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody UpdatePatientControllerRequestDto updatePatientControllerRequestDto) {
        log.info("PATIENT: Update Patient Controller Triggered");

        UpdatePatientServiceRequestDto updatePatientServiceRequestDto = new UpdatePatientServiceRequestDto();
        updatePatientServiceRequestDto.setName(updatePatientControllerRequestDto.getName());
        updatePatientServiceRequestDto.setAddress(updatePatientControllerRequestDto.getAddress());
        updatePatientServiceRequestDto.setDateOfBirth(updatePatientControllerRequestDto.getDateOfBirth());
        updatePatientServiceRequestDto.setEmail(updatePatientControllerRequestDto.getEmail());

        UpdatePatientServiceResponseDto updatePatient = patientService.updatePatient(id, updatePatientServiceRequestDto);

        UpdatePatientControllerResponseDto updatePatientControllerResponseDto = new UpdatePatientControllerResponseDto();
        updatePatientControllerResponseDto.setName(updatePatient.getName());
        updatePatientControllerResponseDto.setAddress(updatePatient.getAddress());
        updatePatientControllerResponseDto.setDateOfBirth(updatePatient.getDateOfBirth());
        updatePatientControllerResponseDto.setEmail(updatePatient.getEmail());


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
        if (currentId.isPresent()) {
            return ResponseEntity.ok(currentId.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/find/email/{email}")
    @Operation(summary = "Find A Patient By Email")
    public ResponseEntity<Boolean> findPatientByEmail(@PathVariable String email) {
        log.info("PATIENT: Find Patient By Email Controller Triggered");
        boolean patientByEmail = patientService.findPatientByEmail(email);
        if(patientByEmail) {return ResponseEntity.ok().body(true);}
        else {return ResponseEntity.notFound().build();}
    }
}
