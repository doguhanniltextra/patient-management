package com.project.inventory_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inventory_service.constants.Endpoints;
import com.project.inventory_service.dto.ItemConsumedEvent;
import com.project.inventory_service.model.Item;
import com.project.inventory_service.model.Stock;
import com.project.inventory_service.repository.ItemRepository;
import com.project.inventory_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Endpoints.INVENTORY_BASE)
@RequiredArgsConstructor
public class InventoryController {
    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.inventory-item-consumed}")
    private String itemConsumedTopic;

    @GetMapping(Endpoints.ITEMS)
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST','ADMIN','PATIENT')")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @PostMapping(Endpoints.ITEMS)
    @PreAuthorize("hasRole('ADMIN')")
    public Item createItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    @GetMapping(Endpoints.STOCKS)
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST','ADMIN')")
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @PostMapping(Endpoints.STOCKS_INITIALIZE)
    @PreAuthorize("hasRole('ADMIN')")
    public Stock initializeStock(@RequestParam UUID itemId, @RequestParam Integer initialQuantity) {
        Stock stock = Stock.builder()
                .itemId(itemId)
                .quantity(initialQuantity)
                .minThreshold(10)
                .build();
        return stockRepository.save(stock);
    }

    // Testing purposes: Trigger consumption event as if it came from another service
    @PostMapping(Endpoints.TEST_CONSUME)
    @PreAuthorize("hasRole('ADMIN') or hasRole('INTERNAL_SERVICE')")
    public String triggerConsumptionEvent(@RequestBody ItemConsumedEvent event) throws JsonProcessingException {
        if (event.getEventId() == null) event.setEventId(UUID.randomUUID().toString());
        if (event.getOccurredAt() == null) event.setOccurredAt(LocalDateTime.now());
        
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send(itemConsumedTopic, event.getItemId().toString(), payload);
        return "Event triggered: " + event.getEventId();
    }
}
