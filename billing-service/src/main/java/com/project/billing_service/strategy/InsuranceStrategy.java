package com.project.billing_service.strategy;

import java.math.BigDecimal;

public interface InsuranceStrategy {
    InsuranceCalculationResult calculate(BigDecimal amount);
}
