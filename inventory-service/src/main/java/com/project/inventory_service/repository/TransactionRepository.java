package com.project.inventory_service.repository;

import com.project.inventory_service.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<StockTransaction, UUID> {
}
