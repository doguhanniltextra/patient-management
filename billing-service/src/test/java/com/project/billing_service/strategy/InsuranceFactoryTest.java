package com.project.billing_service.strategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InsuranceFactoryTest {

    private final SgkStrategy sgkStrategy = new SgkStrategy();
    private final AllianzStrategy allianzStrategy = new AllianzStrategy();
    private final NoInsuranceStrategy noInsuranceStrategy = new NoInsuranceStrategy();

    private final InsuranceFactory insuranceFactory = new InsuranceFactory(sgkStrategy, allianzStrategy, noInsuranceStrategy);

    @Test
    public void InsuranceFactory_getStrategy_ReturnsSgkStrategyForSgkType() {
        InsuranceStrategy strategy = insuranceFactory.getStrategy("SGK", "SGK");

        assertThat(strategy).isInstanceOf(SgkStrategy.class);
    }

    @Test
    public void InsuranceFactory_getStrategy_ReturnsAllianzStrategyForAllianzProvider() {
        InsuranceStrategy strategy = insuranceFactory.getStrategy("PRIVATE", "Allianz");

        assertThat(strategy).isInstanceOf(AllianzStrategy.class);
    }

    @Test
    public void InsuranceFactory_getStrategy_ReturnsNoInsuranceForNoneType() {
        InsuranceStrategy strategy = insuranceFactory.getStrategy("NONE", "NONE");

        assertThat(strategy).isInstanceOf(NoInsuranceStrategy.class);
    }
}
