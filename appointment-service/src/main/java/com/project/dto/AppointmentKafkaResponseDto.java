package com.project.dto;

public class AppointmentKafkaResponseDto {
    private String doctorId;
    private String patientId;
    private double amount;
    private boolean paymentStatus;

    public AppointmentKafkaResponseDto(String doctorId, String patientId, double amount, boolean paymentStatus) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

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
}
