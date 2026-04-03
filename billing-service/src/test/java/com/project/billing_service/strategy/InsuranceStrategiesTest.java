package com.project.billing_service.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class InsuranceStrategiesTest {

    @Test
    public void SgkStrategy_calculate_SplitsEightyTwenty() {
        SgkStrategy strategy = new SgkStrategy();

        InsuranceCalculationResult result = strategy.calculate(new BigDecimal("1000.00"));

        assertThat(result.getInsuranceOwes()).isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(result.getPatientOwes()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    public void AllianzStrategy_calculate_InsurancePaysAll() {
        AllianzStrategy strategy = new AllianzStrategy();

        InsuranceCalculationResult result = strategy.calculate(new BigDecimal("500.00"));

        assertThat(result.getInsuranceOwes()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(result.getPatientOwes()).isEqualByComparingTo(new BigDecimal("0.00"));
    }

    @Test
    public void NoInsuranceStrategy_calculate_PatientPaysAll() {
        NoInsuranceStrategy strategy = new NoInsuranceStrategy();

        InsuranceCalculationResult result = strategy.calculate(new BigDecimal("750.00"));

        assertThat(result.getInsuranceOwes()).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(result.getPatientOwes()).isEqualByComparingTo(new BigDecimal("750.00"));
    }
}
