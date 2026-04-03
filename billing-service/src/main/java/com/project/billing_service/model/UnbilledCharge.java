package com.project.billing_service.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "unbilled_charge", schema = "billing_schema", uniqueConstraints = @UniqueConstraint(columnNames = {"sourceType", "sourceOrderId"}))
public class UnbilledCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID chargeId;
    private UUID patientId;
    private String sourceType;
    private UUID sourceOrderId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private Instant createdAt;

    public UUID getChargeId() { return chargeId; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public UUID getSourceOrderId() { return sourceOrderId; }
    public void setSourceOrderId(UUID sourceOrderId) { this.sourceOrderId = sourceOrderId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
