package com.project.helper;

import com.project.dto.AppointmentKafkaResponseDto;
import com.project.exception.CustomNotFoundException;
import com.project.kafka.KafkaProducer;
import com.project.model.Appointment;
import com.project.repository.AppointmentRepository;
import com.project.utils.IdValidation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class AppointmentValidator {
    private final IdValidation idValidation;

    public AppointmentValidator(IdValidation idValidation) {
        this.idValidation = idValidation;
    }

    public record Result(UUID patientId, UUID doctorId) {
    }

    public void checkDoctorExistsOrNotForCreateAppointment(UUID doctorId) {
        if (!idValidation.checkDoctorExists(doctorId)) {
            throw new CustomNotFoundException("Doctor not found: " + doctorId);
        }
    }

    public void checkPatientExistsOrNotForCreateAppointment(UUID patientId) {
        if (!idValidation.checkPatientExists(patientId)) {
            throw new CustomNotFoundException("Patient not found: " + patientId);
        }
    }


    public static AppointmentKafkaResponseDto getAppointmentKafkaResponseDto(boolean status, Appointment appointment) {
        AppointmentKafkaResponseDto appointmentDTO = new AppointmentKafkaResponseDto(
                appointment.getDoctorId().toString(),
                appointment.getPatientId().toString(),
                appointment.getAmount(),
                status
        );
        return appointmentDTO;
    }


    public void updatePaymentStatusKafkaSendEvent(boolean status, AppointmentKafkaResponseDto appointmentDTO, KafkaProducer kafkaProducer) {
        if (status) {
            kafkaProducer.sendEvent(appointmentDTO);
        }
    }

    public Appointment getAppointmentForUpdatePaymentStatus(UUID id, AppointmentRepository appointmentRepository) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
        return appointment;
    }
    public List<Appointment> getAppointments(List<Appointment> allAppointments, LocalDateTime now, DateTimeFormatter formatter) {
        List<Appointment> outdated = allAppointments.stream()
                .filter(a -> {
                    try {
                        LocalDateTime endDate = LocalDateTime.parse(a.getServiceDateEnd(), formatter);
                        return endDate.isBefore(now);
                    } catch (Exception e) {

                        return false;
                    }
                })
                .toList();
        return outdated;
    }
    public static LocalDateTime getLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        return now;
    }
}
