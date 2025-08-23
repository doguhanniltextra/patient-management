package com.project.service;

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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentCleanupService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // Runs every day at 06:00 AM
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void cleanOutdatedAppointments() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

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

        if (!outdated.isEmpty()) {
            appointmentRepository.deleteAll(outdated);
            System.out.println("✅ Deleted " + outdated.size() + " outdated appointments at " + now);
        }
    }
}
