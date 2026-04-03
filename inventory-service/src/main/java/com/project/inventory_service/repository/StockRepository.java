package com.project.inventory_service.repository;

import com.project.inventory_service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findByItemId(UUID itemId);
}
