package com.project.notification_service.dto;

import java.util.UUID;

public class AppointmentScheduledEvent {
    public UUID patientId;
    public String patientEmail;
    public String patientPhone;
    public UUID doctorId;
    public String appointmentDate;
    public double amount;
}
