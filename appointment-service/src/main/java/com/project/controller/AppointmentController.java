package com.project.controller;

import com.project.constants.Endpoints;
import com.project.dto.AppointmentResponseDTO;
import com.project.dto.AppointmentUpdateDtoResponse;
import com.project.dto.request.CreateAppointmentServiceRequestDto;
import com.project.dto.response.CreateAppointmentServiceResponseDto;
import com.project.helper.AppointmentMapper;
import com.project.utils.IdValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.project.dto.AppointmentDTO;
import com.project.model.Appointment;
import com.project.service.AppointmentService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

@RestController()
@RequestMapping(Endpoints.APPOINTMENT_CONTROLLER_REQUEST)
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final IdValidation idValidation;
    private final AppointmentResponseDTO appointmentResponseDTO;
    private final AppointmentMapper appointmentMapper;

    public AppointmentController(AppointmentService appointmentService, IdValidation idValidation, AppointmentResponseDTO appointmentResponseDTO, AppointmentMapper appointmentMapper) {
        this.appointmentService = appointmentService;
        this.idValidation = idValidation;
        this.appointmentResponseDTO = appointmentResponseDTO;
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping(Endpoints.CREATE_APPOINTMENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('PATIENT', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody Appointment appointment) {
        CreateAppointmentServiceRequestDto requestDto = appointmentMapper.getCreateAppointmentServiceRequestDto(appointment);
        CreateAppointmentServiceResponseDto responseDto = appointmentService.createAppointment(requestDto);
        AppointmentDTO appointmentDTO = appointmentResponseDTO.toDTO(responseDto);

        return ResponseEntity.ok(appointmentDTO);
    }


    @PutMapping(Endpoints.UPDATE_APPOINTMENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST') or @securityService.isAppointmentOwner(authentication, #id)")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment) {
        appointment.setId(id);
        Appointment updatedAppointment = appointmentService.updateAppointment(appointment).getBody();
        AppointmentDTO appointmentDTO = appointmentResponseDTO.toUpdateResponseDTO(updatedAppointment);
        return ResponseEntity.ok(appointmentDTO);
    }

    @PutMapping(Endpoints.UPDATE_APPOINTMENT_STATUS)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentUpdateDtoResponse> UpdateAppointmentStatus(@PathVariable UUID id, @PathVariable boolean status) {
        appointmentService.updatePaymentStatus(id, status);
        AppointmentUpdateDtoResponse appointmentUpdateDtoResponse = new AppointmentUpdateDtoResponse();
        appointmentUpdateDtoResponse.setMessage("Status Updated");
        return ResponseEntity.ok(appointmentUpdateDtoResponse);
    }

    @DeleteMapping(Endpoints.DELETE_APPOINTMENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(Endpoints.GET_ALL_APPOINTMENTS)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Page<Appointment>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, safeSize);
        Page<Appointment> appointments = appointmentService.getAllAppointments(pageable);
        if (appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping(Endpoints.VALIDATE_IDS)
    @org.springframework.security.access.prepost.PreAuthorize("permitAll()")
    public String validateIds(@PathVariable UUID patientId, @PathVariable UUID doctorId) {
        UUID patientIdResult = patientId;
        UUID doctorIdResult = doctorId;

        boolean patientExists = idValidation.checkPatientExists(patientId);
        boolean doctorExists = idValidation.checkDoctorExists(doctorId);

        return "Patient exists: " + patientExists + ", Doctor exists: " + doctorExists;
    }
}
