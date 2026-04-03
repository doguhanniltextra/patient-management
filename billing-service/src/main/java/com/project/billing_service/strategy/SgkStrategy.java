package com.project.billing_service.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SgkStrategy implements InsuranceStrategy {
    @Override
    public InsuranceCalculationResult calculate(BigDecimal amount) {
        BigDecimal insuranceOwes = amount.multiply(BigDecimal.valueOf(0.80)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal patientOwes = amount.subtract(insuranceOwes).setScale(2, RoundingMode.HALF_UP);
        return new InsuranceCalculationResult(patientOwes, insuranceOwes);
    }
}
