package com.project.billing_service.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class AllianzStrategy implements InsuranceStrategy {
    @Override
    public InsuranceCalculationResult calculate(BigDecimal amount) {
        BigDecimal insuranceOwes = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal patientOwes = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return new InsuranceCalculationResult(patientOwes, insuranceOwes);
    }
}
