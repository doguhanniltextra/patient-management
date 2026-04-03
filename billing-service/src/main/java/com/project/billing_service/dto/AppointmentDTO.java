package com.project.billing_service.dto;


public class AppointmentDTO {
    private String doctorId;
    private String patientId;
    private double amount;
    private boolean paymentStatus;
    private String insuranceProviderType;
    private String providerName;

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getInsuranceProviderType() {
        return insuranceProviderType;
    }

    public void setInsuranceProviderType(String insuranceProviderType) {
        this.insuranceProviderType = insuranceProviderType;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}
