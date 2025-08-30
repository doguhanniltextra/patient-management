package com.project.controller;

import com.project.constants.Endpoints;
import com.project.constants.SwaggerMessages;
import com.project.dto.UpdateDoctorControllerRequestDto;
import com.project.dto.UpdateDoctorControllerResponseDto;
import com.project.dto.UpdateDoctorServiceRequestDto;
import com.project.dto.UpdateDoctorServiceResponseDto;
import com.project.dto.response.CreateDoctorControllerResponseDto;
import com.project.dto.response.CreateDoctorServiceResponseDto;
import com.project.dto.request.CreateDoctorControllerRequestDto;
import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.exception.EmailIsNotUniqueException;
import com.project.exception.IdIsValidException.IdIsValidException;
import com.project.helper.DoctorMapper;
import com.project.helper.DoctorValidator;
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
@RequestMapping(Endpoints.DOCTOR_CONTROLLER_REQUEST)
@Tag(name = SwaggerMessages.DOCTOR_CONTROLLER_NAME, description = SwaggerMessages.DOCTOR_CONTROLLER_DESCRIPTION)
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final DoctorValidator doctorValidator;


    public DoctorController(DoctorService doctorService, DoctorMapper doctorMapper, DoctorValidator doctorValidator) {
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.doctorValidator = doctorValidator;
    }

    @GetMapping
    @Operation(summary = SwaggerMessages.GET_DOCTORS )
    public ResponseEntity<List<Doctor>> getDoctors() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok().body(doctors);
    }
    

    @PutMapping(Endpoints.DOCTOR_CONTROLLER_UPDATE_DOCTOR)
    @Operation(summary = SwaggerMessages.UPDATE_DOCTOR)
    public ResponseEntity<UpdateDoctorControllerResponseDto> updateDoctor(@PathVariable UUID id, @Validated({Default.class}) @RequestBody UpdateDoctorControllerRequestDto updateDoctorControllerRequestDto) {

        UpdateDoctorServiceRequestDto updateDoctorServiceRequestDto = new UpdateDoctorServiceRequestDto();
        updateDoctorServiceRequestDto.setEmail(updateDoctorControllerRequestDto.getEmail());
        updateDoctorServiceRequestDto.setName(updateDoctorControllerRequestDto.getName());
        updateDoctorServiceRequestDto.setHospitalName(updateDoctorControllerRequestDto.getHospitalName());
        updateDoctorServiceRequestDto.setSpecialization(updateDoctorControllerRequestDto.getSpecialization());
        updateDoctorServiceRequestDto.setLicenseNumber(updateDoctorControllerRequestDto.getLicenseNumber());
        
        
        UpdateDoctorServiceResponseDto updated_doctor = doctorService.updateDoctor(id, updateDoctorServiceRequestDto);

        UpdateDoctorControllerResponseDto updateDoctorControllerResponseDto = getUpdateDoctorControllerResponseDto(updated_doctor);

        return ResponseEntity.ok().body(updateDoctorControllerResponseDto);
    }

    private static UpdateDoctorControllerResponseDto getUpdateDoctorControllerResponseDto(UpdateDoctorServiceResponseDto updateDoctorServiceResponseDto) {
        UpdateDoctorControllerResponseDto updateDoctorControllerResponseDto = DoctorMapper.getDoctorControllerResponseDto(updateDoctorServiceResponseDto);
        return updateDoctorControllerResponseDto;
    }

    @PostMapping
    @Operation(summary = SwaggerMessages.CREATE_DOCTOR)
    public ResponseEntity<CreateDoctorControllerResponseDto> createDoctor(@Valid @RequestBody CreateDoctorControllerRequestDto createDoctorControllerRequestDto) throws IdIsValidException, EmailIsNotUniqueException {

        CreateDoctorServiceRequestDto createDoctorServiceRequestDto = doctorMapper.getCreateDoctorServiceRequestDto(createDoctorControllerRequestDto);
        CreateDoctorServiceResponseDto createdDoctor = doctorService.createDoctor(createDoctorServiceRequestDto);
        CreateDoctorControllerResponseDto createDoctorControllerResponseDto = getCreateDoctorControllerResponseDto(createdDoctor);

        return ResponseEntity.ok().body(createDoctorControllerResponseDto);
    }

    private static CreateDoctorControllerResponseDto getCreateDoctorControllerResponseDto(CreateDoctorServiceResponseDto createdDoctor) {
        CreateDoctorControllerResponseDto createDoctorControllerResponseDto = new CreateDoctorControllerResponseDto();
        createDoctorControllerResponseDto.setId(createdDoctor.getId());
        createDoctorControllerResponseDto.setName(createdDoctor.getName());
        createDoctorControllerResponseDto.setEmail(createdDoctor.getEmail());
        createDoctorControllerResponseDto.setNumber(createdDoctor.getNumber());
        createDoctorControllerResponseDto.setHospitalName(createdDoctor.getHospitalName());
        return createDoctorControllerResponseDto;
    }


    @DeleteMapping(Endpoints.DOCTOR_CONTROLLER_DELETE_DOCTOR)
    @Operation(summary = SwaggerMessages.DELETE_DOCTOR)
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Endpoints.DOCTOR_CONTROLLER_FIND_DOCTOR_BY_ID)
    @Operation(summary = SwaggerMessages.FIND_DOCTOR_BY_ID)
    public ResponseEntity<Doctor> findDoctorById(@PathVariable UUID id) {
        Optional<Doctor> currentId = doctorService.findDoctorById(id);
        if (currentId.isPresent()) {
            return ResponseEntity.ok(currentId.get()); 
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
