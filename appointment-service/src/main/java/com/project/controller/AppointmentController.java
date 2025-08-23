package com.project.controller;

import com.project.dto.AppointmentResponseDTO;
import com.project.dto.AppointmentUpdateDtoResponse;
import com.project.utils.IdValidation;
import org.springframework.web.bind.annotation.*;

import com.project.dto.AppointmentDTO;
import com.project.model.Appointment;
import com.project.service.AppointmentService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

@RestController()
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final IdValidation idValidation;
    private final AppointmentResponseDTO appointmentResponseDTO;

    public AppointmentController(AppointmentService appointmentService, IdValidation idValidation, AppointmentResponseDTO appointmentResponseDTO) {
        this.appointmentService = appointmentService;
        this.idValidation = idValidation;
        this.appointmentResponseDTO = appointmentResponseDTO;
    }

    @PostMapping("/")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody Appointment appointment) {
        Appointment createdAppointment = appointmentService.createAppointment(appointment);
        AppointmentDTO appointmentDTO = appointmentResponseDTO.toDTO(createdAppointment);
        return ResponseEntity.ok(appointmentDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment) {
        appointment.setId(id);
        Appointment updatedAppointment = appointmentService.updateAppointment(appointment).getBody();
        AppointmentDTO appointmentDTO = appointmentResponseDTO.toDTO(updatedAppointment);
        return ResponseEntity.ok(appointmentDTO);
    }

    @PutMapping("/appointment-update/{id}/{status}")
    public ResponseEntity<AppointmentUpdateDtoResponse> UpdateAppointmentStatus(@PathVariable UUID id, @PathVariable boolean status) {
        appointmentService.updatePaymentStatus(id, status);
        AppointmentUpdateDtoResponse appointmentUpdateDtoResponse = new AppointmentUpdateDtoResponse();
        appointmentUpdateDtoResponse.setMessage("Status Updated");
        return ResponseEntity.ok(appointmentUpdateDtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        if (appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/validate/{patientId}/{doctorId}")
    public String validateIds(@PathVariable UUID patientId, @PathVariable UUID doctorId) {
        UUID patientIdResult = patientId;
        UUID doctorIdResult = doctorId;

        boolean patientExists = idValidation.checkPatientExists(patientId);
        boolean doctorExists = idValidation.checkDoctorExists(doctorId);

        return "Patient exists: " + patientExists + ", Doctor exists: " + doctorExists;
    }
}
