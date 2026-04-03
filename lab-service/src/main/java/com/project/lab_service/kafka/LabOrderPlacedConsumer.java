package com.project.lab_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.lab_service.constants.KafkaTopics;
import com.project.lab_service.dto.LabOrderPlacedEvent;
import com.project.lab_service.model.ProcessedEvent;
import com.project.lab_service.repository.ProcessedEventRepository;
import com.project.lab_service.service.LabWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class LabOrderPlacedConsumer {
    private static final Logger log = LoggerFactory.getLogger(LabOrderPlacedConsumer.class);
    
    @Value("${kafka.groups.lab-order:lab-order-group}")
    private String consumerGroup;
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository processedEventRepository;
    private final LabWorkflowService workflowService;

    public LabOrderPlacedConsumer(
            ObjectMapper objectMapper,
            ProcessedEventRepository processedEventRepository, 
            LabWorkflowService workflowService) {
        this.objectMapper = objectMapper;
        this.processedEventRepository = processedEventRepository;
        this.workflowService = workflowService;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.LAB_ORDER_PLACED, groupId = KafkaTopics.LAB_ORDER_GROUP)
    public void listen(String message) throws Exception {
        log.info("Received LabOrderPlacedEvent message: {}", message);
        LabOrderPlacedEvent event = objectMapper.readValue(message, LabOrderPlacedEvent.class);
        log.info("Parsed event: orderId={}, patientId={}", event.orderId, event.patientId);
        
        if (processedEventRepository.findByEventIdAndConsumerName(event.eventId, consumerGroup).isPresent()) {
            log.info("Event already processed: {}", event.eventId);
            return;
        }
        
        workflowService.createFromPlacedEvent(event);
        log.info("Lab order created in workflow service for orderId: {}", event.orderId);

        ProcessedEvent marker = new ProcessedEvent();
        marker.setEventId(event.eventId);
        marker.setConsumerName(consumerGroup);
        marker.setProcessedAt(Instant.now());
        processedEventRepository.save(marker);
        log.info("Event marker saved for eventId: {}", event.eventId);
    }
}
