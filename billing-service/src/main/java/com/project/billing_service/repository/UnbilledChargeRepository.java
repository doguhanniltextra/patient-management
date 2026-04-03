package com.project.billing_service.repository;

import com.project.billing_service.model.UnbilledCharge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UnbilledChargeRepository extends JpaRepository<UnbilledCharge, UUID> {
    Optional<UnbilledCharge> findBySourceTypeAndSourceOrderId(String sourceType, UUID sourceOrderId);
}
