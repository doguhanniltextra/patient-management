package com.project.inventory_service.service;

import com.project.inventory_service.model.*;
import com.project.inventory_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void consumeItem(UUID itemId, Integer quantity, UUID referenceId) {
        Stock stock = stockRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Stock not found for item: " + itemId));

        if (stock.getQuantity() < quantity) {
            log.warn("Low stock alert! Attempting to consume {} but only {} available for item {}", 
                     quantity, stock.getQuantity(), itemId);
            // In medical systems, we often allow "negative" stock with a critical warning 
            // rather than failing a patient care action.
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);

        StockTransaction tx = StockTransaction.builder()
                .itemId(itemId)
                .quantityChange(-quantity)
                .type(TransactionType.CONSUMPTION)
                .reason("Patient Consumption")
                .referenceId(referenceId)
                .build();
        transactionRepository.save(tx);

        if (stock.getQuantity() <= stock.getMinThreshold()) {
            log.error("CRITICAL: Item {} stock ({}) is below threshold ({})!", 
                      itemId, stock.getQuantity(), stock.getMinThreshold());
            // Here you would typically integrate with a notification service
        }
    }
}
