package com.project.billing_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.billing_service.dto.AppointmentDTO;
import com.project.billing_service.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InvoiceService invoiceService;

    public KafkaConsumer(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @KafkaListener(topics = "appointment-payment-updated", groupId = "appointment-group")
    public void listen(String message) {
        try {
            AppointmentDTO appointment = objectMapper.readValue(message, AppointmentDTO.class);
            log.info("Received Appointment: {}", appointment);

            String invoiceNumber = appointment.getPatientId().substring(0, 8) + "-" + appointment.getDoctorId().substring(0, 8);
            Path invoicePath = getPath(appointment, invoiceNumber);

            log.info("Invoice generated at: {}", invoicePath.toAbsolutePath());

        } catch (Exception e) {
            log.error("Failed to parse Appointment JSON or generate invoice", e);
        }
    }

    private Path getPath(AppointmentDTO appointment, String invoiceNumber) {
        Path invoicePath = invoiceService.generateInvoice(
                "Dr. " + appointment.getDoctorId(),
                "Patient " + appointment.getPatientId(),
                appointment.getAmount(),
                invoiceNumber
        );
        return invoicePath;
    }
}
