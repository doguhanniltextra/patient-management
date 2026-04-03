package com.project.billing_service.repository;

import com.project.billing_service.model.Claim;
import com.project.billing_service.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByStatus(ClaimStatus status);
}
