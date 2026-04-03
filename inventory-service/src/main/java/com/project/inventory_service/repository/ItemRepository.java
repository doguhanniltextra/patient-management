package com.project.inventory_service.repository;

import com.project.inventory_service.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByCode(String code);
}
