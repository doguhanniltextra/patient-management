package com.project.billing_service.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class NoInsuranceStrategy implements InsuranceStrategy {
    @Override
    public InsuranceCalculationResult calculate(BigDecimal amount) {
        BigDecimal patientOwes = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal insuranceOwes = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return new InsuranceCalculationResult(patientOwes, insuranceOwes);
    }
}
