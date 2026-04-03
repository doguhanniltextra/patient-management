package com.project.inventory_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inventory_service.dto.ItemConsumedEvent;
import com.project.inventory_service.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public KafkaConsumer(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.inventory-item-consumed}", groupId = "inventory-group")
    public void consumeItemConsumedEvent(String message) throws Exception {
        ItemConsumedEvent event = objectMapper.readValue(message, ItemConsumedEvent.class);
        log.info("Received ItemConsumedEvent for item: {} quantity: {}", 
                 event.getItemId(), event.getQuantity());
        
        inventoryService.consumeItem(
            event.getItemId(), 
            event.getQuantity(), 
            java.util.UUID.fromString(event.getEventId())
        );
    }
}
