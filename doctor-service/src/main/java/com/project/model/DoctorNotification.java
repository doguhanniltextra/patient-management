package com.project.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "doctor_notifications", schema = "doctor_schema", uniqueConstraints = @UniqueConstraint(columnNames = {"eventId"}))
public class DoctorNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID doctorId;
    private String message;
    private String eventId;
    private Instant createdAt;

    public UUID getId() { return id; }
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
