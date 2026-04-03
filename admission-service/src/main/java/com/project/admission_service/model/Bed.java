package com.project.admission_service.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "beds", schema = "admission_schema")
public class Bed {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID roomId;
    private String bedNumber;
    
    @Enumerated(EnumType.STRING)
    private BedStatus status;

    public Bed() {
    }

    public Bed(UUID id, UUID roomId, String bedNumber, BedStatus status) {
        this.id = id;
        this.roomId = roomId;
        this.bedNumber = bedNumber;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public BedStatus getStatus() {
        return status;
    }

    public void setStatus(BedStatus status) {
        this.status = status;
    }
}
