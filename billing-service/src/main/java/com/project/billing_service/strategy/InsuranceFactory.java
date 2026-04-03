package com.project.billing_service.strategy;

import org.springframework.stereotype.Component;

@Component
public class InsuranceFactory {
    private final SgkStrategy sgkStrategy;
    private final AllianzStrategy allianzStrategy;
    private final NoInsuranceStrategy noInsuranceStrategy;

    public InsuranceFactory(SgkStrategy sgkStrategy, AllianzStrategy allianzStrategy, NoInsuranceStrategy noInsuranceStrategy) {
        this.sgkStrategy = sgkStrategy;
        this.allianzStrategy = allianzStrategy;
        this.noInsuranceStrategy = noInsuranceStrategy;
    }

    public InsuranceStrategy getStrategy(String providerType, String providerName) {
        if (providerType != null && providerType.equalsIgnoreCase("NONE")) {
            return noInsuranceStrategy;
        }
        if (providerType != null && providerType.equalsIgnoreCase("SGK")) {
            return sgkStrategy;
        }
        if (providerName != null && providerName.equalsIgnoreCase("allianz")) {
            return allianzStrategy;
        }
        if (providerType != null && providerType.equalsIgnoreCase("PRIVATE")) {
            return noInsuranceStrategy;
        }
        return noInsuranceStrategy;
    }
}
