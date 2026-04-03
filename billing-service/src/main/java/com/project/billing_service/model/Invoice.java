package com.project.billing_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "invoices", schema = "billing_schema")
public class Invoice {
    @Id
    private UUID invoiceId;
    private UUID patientId;
    private UUID doctorId;
    private BigDecimal totalAmount;
    private BigDecimal patientOwes;
    private BigDecimal insuranceOwes;
    @Column(length = 1024)
    private String invoicePdfUrl;

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPatientOwes() {
        return patientOwes;
    }

    public void setPatientOwes(BigDecimal patientOwes) {
        this.patientOwes = patientOwes;
    }

    public BigDecimal getInsuranceOwes() {
        return insuranceOwes;
    }

    public void setInsuranceOwes(BigDecimal insuranceOwes) {
        this.insuranceOwes = insuranceOwes;
    }

    public String getInvoicePdfUrl() {
        return invoicePdfUrl;
    }

    public void setInvoicePdfUrl(String invoicePdfUrl) {
        this.invoicePdfUrl = invoicePdfUrl;
    }
}
