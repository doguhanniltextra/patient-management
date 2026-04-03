package com.project.service;

import com.project.constants.KafkaTopics;
import com.project.model.DoctorOutboxEvent;
import com.project.repository.DoctorOutboxEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class DoctorOutboxPublisher {
    private final DoctorOutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    @Value("${kafka.topics.lab-order-placed:lab-order-placed.v1}")
    private String labOrderPlacedTopic;

    public DoctorOutboxPublisher(DoctorOutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @Scheduled(fixedDelayString = "${doctor.outbox.publisher.delay-ms:3000}")
    public void publishPendingEvents() {
        List<DoctorOutboxEvent> pending = outboxEventRepository.claimPending(Instant.now());
        for (DoctorOutboxEvent event : pending) {
            try {
                event.setStatus("PROCESSING");
                outboxEventRepository.save(event);
                kafkaTemplate.send(labOrderPlacedTopic, event.getAggregateId(), event.getPayloadJson());
                event.setStatus("SENT");
                outboxEventRepository.save(event);
            } catch (Exception e) {
                int retry = event.getRetryCount() + 1;
                event.setRetryCount(retry);
                event.setStatus(retry >= 10 ? "FAILED" : "PENDING");
                event.setNextRetryAt(Instant.now().plusSeconds((long) Math.pow(2, Math.min(retry, 8))));
                outboxEventRepository.save(event);
            }
        }
    }
}
