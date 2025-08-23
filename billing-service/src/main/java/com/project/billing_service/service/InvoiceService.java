package com.project.billing_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


    @Service
    public class InvoiceService {

        private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);
        private final RestTemplate restTemplate;

        private final String API_URL = "https://invoice-generator.com";

        @Value("${invoice.api.key}")
        private String apiKey;

        public InvoiceService(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        public Path generateInvoice(String doctorName, String patientName, double amount, String invoiceNumber) {
            Map<String, Object> body = new HashMap<>();

            log.info("Generate Invoice Method Triggered");

            body.put("from", doctorName);
            body.put("to", patientName);
            body.put("number", invoiceNumber);
            body.put("date", LocalDate.now().toString());
            body.put("due_date", LocalDate.now().plusDays(7).toString());

            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("name", "Medical Service");
            item.put("quantity", 1);
            item.put("unit_cost", amount);
            item.put("notes","Thanks for purchasing!");
            items.add(item);

            body.put("items", items);
            log.info(items.toString());

            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> response = restTemplate.postForEntity(API_URL, request, byte[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Invoice generation failed");
            }

            Path invoiceDir = Paths.get("invoices");

            if (!Files.exists(invoiceDir)) {
                try {
                    Files.createDirectories(invoiceDir);
                } catch (IOException e) {
                    throw new RuntimeException("Cannot create invoice directory", e);
                }
            }

            Path invoicePath = invoiceDir.resolve(invoiceNumber + ".pdf");

            try {
                Files.write(invoicePath, response.getBody());
                log.info("PDF is crated");
            } catch (IOException e) {
                throw new RuntimeException("Failed to save invoice PDF", e);
            }

            return invoicePath;
        }
}

