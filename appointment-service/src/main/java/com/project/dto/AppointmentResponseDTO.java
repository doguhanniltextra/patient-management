package com.project.dto;

import com.project.dto.response.CreateAppointmentServiceResponseDto;
import com.project.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentResponseDTO {
    public AppointmentDTO toDTO(CreateAppointmentServiceResponseDto createAppointmentServiceResponseDto) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAmount(createAppointmentServiceResponseDto.getAmount());
        dto.setPaymentStatus(createAppointmentServiceResponseDto.isPaymentStatus());
        dto.setServiceDate(createAppointmentServiceResponseDto.getServiceDate());
        dto.setServiceType(createAppointmentServiceResponseDto.getServiceType());
        return dto;
    }

    public AppointmentDTO toUpdateResponseDTO(Appointment updatedAppointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAmount(updatedAppointment.getAmount());
        dto.setPaymentStatus(updatedAppointment.isPaymentStatus());
        dto.setServiceDate(updatedAppointment.getServiceDate());
        dto.setServiceType(updatedAppointment.getServiceType());
        return dto;
    }
}
