package com.project.inventory_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inventory_service.dto.ItemConsumedEvent;
import com.project.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.inventory-item-consumed}", groupId = "inventory-group")
    public void consumeItemConsumedEvent(String message) {
        try {
            ItemConsumedEvent event = objectMapper.readValue(message, ItemConsumedEvent.class);
            log.info("Received ItemConsumedEvent for item: {} quantity: {}", 
                     event.getItemId(), event.getQuantity());
            
            inventoryService.consumeItem(
                event.getItemId(), 
                event.getQuantity(), 
                java.util.UUID.fromString(event.getEventId())
            );
            
        } catch (Exception e) {
            log.error("Failed to process ItemConsumedEvent", e);
        }
    }
}
