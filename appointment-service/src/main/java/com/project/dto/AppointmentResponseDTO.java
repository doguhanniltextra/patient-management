package com.project.dto;

import com.project.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentResponseDTO {
    public AppointmentDTO toDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAmount(appointment.getAmount());
        dto.setPaymentStatus(appointment.isPaymentStatus());
        dto.setServiceDate(appointment.getServiceDate());
        return dto;
    }
}
