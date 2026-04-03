package com.project.controller;

import com.project.constants.Endpoints;
import com.project.constants.SwaggerMessages;
import com.project.dto.UpdateDoctorControllerRequestDto;
import com.project.dto.UpdateDoctorControllerResponseDto;
import com.project.dto.UpdateDoctorServiceRequestDto;
import com.project.dto.UpdateDoctorServiceResponseDto;
import com.project.dto.request.CreateLeaveRequestDto;
import com.project.dto.request.CreateShiftRequestDto;
import com.project.dto.response.AvailabilityResponseDto;
import com.project.dto.response.CreateDoctorControllerResponseDto;
import com.project.dto.response.CreateDoctorServiceResponseDto;
import com.project.dto.response.DoctorAvailabilitySummaryDto;
import com.project.dto.response.LeaveResponseDto;
import com.project.dto.response.ShiftResponseDto;
import com.project.dto.request.CreateDoctorControllerRequestDto;
import com.project.dto.request.CreateDoctorServiceRequestDto;
import com.project.exception.DoctorNotFoundException;
import com.project.exception.EmailIsNotUniqueException;
import com.project.exception.IdIsValidException.IdIsValidException;
import com.project.helper.DoctorMapper;
import com.project.helper.DoctorValidator;
import com.project.model.Doctor;
import com.project.model.ServiceType;
import com.project.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Page<Doctor>> getDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, safeSize);
        Page<Doctor> doctors = doctorService.getDoctors(pageable);
        return ResponseEntity.ok().body(doctors);
    }
    

    @PutMapping(Endpoints.DOCTOR_CONTROLLER_UPDATE_DOCTOR)
    @Operation(summary = SwaggerMessages.UPDATE_DOCTOR)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or @securityService.isDoctorOwner(authentication, #id)")
    public ResponseEntity<UpdateDoctorControllerResponseDto> updateDoctor(@PathVariable UUID id, @Validated({Default.class}) @RequestBody UpdateDoctorControllerRequestDto updateDoctorControllerRequestDto) throws DoctorNotFoundException {

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
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
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
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Endpoints.DOCTOR_CONTROLLER_FIND_DOCTOR_BY_ID)
    @Operation(summary = SwaggerMessages.FIND_DOCTOR_BY_ID)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'INTERNAL_SERVICE') or @securityService.isDoctorOwner(authentication, #id)")
    public ResponseEntity<Doctor> findDoctorById(@PathVariable UUID id) {
        Optional<Doctor> currentId = doctorService.findDoctorById(id);
        if (currentId.isPresent()) {
            return ResponseEntity.ok(currentId.get()); 
        } else {
            return ResponseEntity.notFound().build();
    }
}

    @PutMapping(Endpoints.DOCTOR_CONTROLLER_INCREASE_PATIENT)
    @Operation(summary = "Increase patient count for a doctor")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('PATIENT', 'RECEPTIONIST', 'ADMIN', 'INTERNAL_SERVICE')")
    public ResponseEntity<Void> increasePatientNumber(@PathVariable UUID id) throws com.project.exception.PatientLimitException, DoctorNotFoundException {
        doctorService.increasePatientNumber(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(Endpoints.DOCTOR_CONTROLLER_SHIFTS)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or @securityService.isDoctorOwner(authentication, #doctorId)")
    public ResponseEntity<ShiftResponseDto> createShift(@PathVariable UUID doctorId, @Valid @RequestBody CreateShiftRequestDto requestDto) {
        return ResponseEntity.ok(doctorService.createShift(doctorId, requestDto));
    }

    @GetMapping(Endpoints.DOCTOR_CONTROLLER_SHIFTS)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @securityService.isDoctorOwner(authentication, #doctorId)")
    public ResponseEntity<List<ShiftResponseDto>> listShifts(
            @PathVariable UUID doctorId,
            @RequestParam String fromDate,
            @RequestParam String toDate
    ) {
        try {
            LocalDate from = LocalDate.parse(fromDate);
            LocalDate to = LocalDate.parse(toDate);
            return ResponseEntity.ok(doctorService.listShifts(doctorId, from, to));
        } catch (DateTimeParseException ex) {
            throw new com.project.exception.ApiException("INVALID_SLOT", "Invalid date format, expected yyyy-MM-dd", 400);
        }
    }

    @DeleteMapping(Endpoints.DOCTOR_CONTROLLER_SHIFT_BY_ID)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or @securityService.isDoctorOwner(authentication, #doctorId)")
    public ResponseEntity<Void> deleteShift(@PathVariable UUID doctorId, @PathVariable UUID shiftId) {
        doctorService.deleteShift(doctorId, shiftId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(Endpoints.DOCTOR_CONTROLLER_LEAVES)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or @securityService.isDoctorOwner(authentication, #doctorId)")
    public ResponseEntity<LeaveResponseDto> createLeave(@PathVariable UUID doctorId, @Valid @RequestBody CreateLeaveRequestDto requestDto) {
        return ResponseEntity.ok(doctorService.createLeave(doctorId, requestDto));
    }

    @PutMapping(Endpoints.DOCTOR_CONTROLLER_LEAVE_APPROVE)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaveResponseDto> approveLeave(@PathVariable UUID doctorId, @PathVariable UUID leaveId) {
        return ResponseEntity.ok(doctorService.approveLeave(doctorId, leaveId));
    }

    @DeleteMapping(Endpoints.DOCTOR_CONTROLLER_LEAVE_BY_ID)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or @securityService.isOwnPendingLeave(authentication, #doctorId, #leaveId)")
    public ResponseEntity<Void> deleteLeave(@PathVariable UUID doctorId, @PathVariable UUID leaveId) {
        doctorService.deleteLeave(doctorId, leaveId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Endpoints.DOCTOR_CONTROLLER_AVAILABILITY_BY_DOCTOR)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'PATIENT', 'DOCTOR', 'INTERNAL_SERVICE')")
    public ResponseEntity<AvailabilityResponseDto> checkAvailability(
            @PathVariable UUID doctorId,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam ServiceType serviceType
    ) {
        return ResponseEntity.ok(doctorService.checkDoctorAvailability(doctorId, start, end, serviceType));
    }

    @GetMapping(Endpoints.DOCTOR_CONTROLLER_AVAILABILITY)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'PATIENT', 'DOCTOR', 'INTERNAL_SERVICE')")
    public ResponseEntity<Page<DoctorAvailabilitySummaryDto>> getAvailableDoctors(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam ServiceType serviceType,
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(doctorService.findAvailableDoctorsForSlot(start, end, serviceType, specialization, pageable));
    }

}
