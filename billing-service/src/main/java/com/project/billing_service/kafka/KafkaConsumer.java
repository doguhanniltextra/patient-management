package com.project.billing_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.billing_service.constants.KafkaTopics;
import com.project.billing_service.constants.LogMessages;
import com.project.billing_service.dto.AppointmentDTO;
import com.project.billing_service.service.BillingWorkflowService;
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
}
