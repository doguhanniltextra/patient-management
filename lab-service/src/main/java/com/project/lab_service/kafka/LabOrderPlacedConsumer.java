package com.project.lab_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.lab_service.constants.KafkaTopics;
import com.project.lab_service.dto.LabOrderPlacedEvent;
import com.project.lab_service.model.ProcessedEvent;
import com.project.lab_service.repository.ProcessedEventRepository;
import com.project.lab_service.service.LabWorkflowService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class LabOrderPlacedConsumer {
    @Value("${kafka.groups.lab-order:lab-order-group}")
    private String consumerGroup;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProcessedEventRepository processedEventRepository;
    private final LabWorkflowService workflowService;

    public LabOrderPlacedConsumer(ProcessedEventRepository processedEventRepository, LabWorkflowService workflowService) {
        this.processedEventRepository = processedEventRepository;
        this.workflowService = workflowService;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.LAB_ORDER_PLACED, groupId = KafkaTopics.LAB_ORDER_GROUP)
    public void listen(String message) throws Exception {
        LabOrderPlacedEvent event = objectMapper.readValue(message, LabOrderPlacedEvent.class);
        if (processedEventRepository.findByEventIdAndConsumerName(event.eventId, consumerGroup).isPresent()) {
            return;
        }
        workflowService.createFromPlacedEvent(event);
        ProcessedEvent marker = new ProcessedEvent();
        marker.setEventId(event.eventId);
        marker.setConsumerName(consumerGroup);
        marker.setProcessedAt(Instant.now());
        processedEventRepository.save(marker);
    }
}
