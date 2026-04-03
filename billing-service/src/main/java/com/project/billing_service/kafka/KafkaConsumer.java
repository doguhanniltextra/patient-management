package com.project.billing_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.billing_service.constants.KafkaTopics;
import com.project.billing_service.constants.LogMessages;
import com.project.billing_service.dto.AppointmentDTO;
import com.project.billing_service.service.BillingWorkflowService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BillingWorkflowService billingWorkflowService;

    public KafkaConsumer(BillingWorkflowService billingWorkflowService) {
        this.billingWorkflowService = billingWorkflowService;
    }

    @KafkaListener(topics = KafkaTopics.APPOINTMENT_PAYMENT_UPDATED, groupId = KafkaTopics.APPOINTMENT_GROUP)
    public void listen(String message) {
        try {
            AppointmentDTO appointment = objectMapper.readValue(message, AppointmentDTO.class);
            log.info(LogMessages.LISTENER_RECEIVED_MESSAGE, appointment);
            billingWorkflowService.processPaymentUpdate(appointment);
            log.info(LogMessages.INVOICE_GENERATED, appointment.getPatientId());

        } catch (Exception e) {
            log.error(LogMessages.FAILED_TO_PARSE_OR_GENERATE_INVOICE, e);
        }
    }

    @KafkaListener(topics = KafkaTopics.LAB_ORDER_PLACED, groupId = KafkaTopics.LAB_ORDER_GROUP)
    public void listenLabOrder(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            billingWorkflowService.createUnbilledLabCharge(
                    java.util.UUID.fromString(event.get("patientId").asText()),
                    java.util.UUID.fromString(event.get("orderId").asText()),
                    new java.math.BigDecimal(event.get("orderTotal").asText()),
                    "TRY"
            );
        } catch (Exception e) {
            log.error("Failed to process lab order event", e);
        }
    }

    @KafkaListener(topics = KafkaTopics.INVENTORY_ITEM_CONSUMED, groupId = "billing-inventory-group")
    public void listenInventoryConsumption(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            billingWorkflowService.createUnbilledInventoryCharge(
                    java.util.UUID.fromString(event.get("patientId").asText()),
                    java.util.UUID.fromString(event.get("itemId").asText()),
                    event.get("quantity").asInt(),
                    new java.math.BigDecimal(event.get("unitPriceSnapshot").asText()),
                    event.get("currency").asText(),
                    java.util.UUID.fromString(event.get("eventId").asText())
            );
        } catch (Exception e) {
            log.error("Failed to process inventory consumption event", e);
        }
    }

    @KafkaListener(topics = KafkaTopics.ADMISSION_BED_CHARGE, groupId = "billing-admission-group")
    public void listenBedCharge(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            billingWorkflowService.createUnbilledBedCharge(
                    java.util.UUID.fromString(event.get("patientId").asText()),
                    java.util.UUID.fromString(event.get("admissionId").asText()),
                    new java.math.BigDecimal(event.get("amount").asText()),
                    event.get("currency").asText(),
                    java.util.UUID.fromString(event.get("eventId").asText())
            );
        } catch (Exception e) {
            log.error("Failed to process bed charge event", e);
        }
    }

    @KafkaListener(topics = KafkaTopics.ADMISSION_DISCHARGED, groupId = "billing-admission-group")
    public void listenDischarge(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            billingWorkflowService.finalizeDischargeBilling(
                    java.util.UUID.fromString(event.get("patientId").asText()),
                    java.util.UUID.fromString(event.get("admissionId").asText())
            );
        } catch (Exception e) {
            log.error("Failed to process patient discharge event", e);
        }
    }
}
