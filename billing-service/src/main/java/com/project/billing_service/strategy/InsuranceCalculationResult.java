package com.project.billing_service.strategy;

import java.math.BigDecimal;

public class InsuranceCalculationResult {
    private final BigDecimal patientOwes;
    private final BigDecimal insuranceOwes;

    public InsuranceCalculationResult(BigDecimal patientOwes, BigDecimal insuranceOwes) {
        this.patientOwes = patientOwes;
        this.insuranceOwes = insuranceOwes;
    }

    public BigDecimal getPatientOwes() {
        return patientOwes;
    }

    public BigDecimal getInsuranceOwes() {
        return insuranceOwes;
    }
}
