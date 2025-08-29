package com.project.service;

import com.project.helper.AppointmentValidator;
import com.project.model.Appointment;
import com.project.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AppointmentCleanupService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentValidator appointmentValidator;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentCleanupService(AppointmentRepository appointmentRepository, AppointmentValidator appointmentValidator) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentValidator = appointmentValidator;
    }

    // Runs every day at 06:00 AM
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void cleanOutdatedAppointments() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        LocalDateTime now = appointmentValidator.getLocalDateTime();

        List<Appointment> outdated = appointmentValidator.getAppointments(allAppointments, now, formatter);

        if (!outdated.isEmpty()) {
            appointmentRepository.deleteAll(outdated);
            System.out.println("✅ Deleted " + outdated.size() + " outdated appointments at " + now);
        }
    }
}
