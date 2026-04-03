package com.project.lab_service.service;

import com.project.lab_service.dto.LabOrderPlacedEvent;
import com.project.lab_service.dto.LabResultCompletedEvent;
import com.project.lab_service.model.LabOrder;
import com.project.lab_service.model.LabOrderStatus;
import com.project.lab_service.model.TestResult;
import com.project.lab_service.repository.LabOrderRepository;
import com.project.lab_service.repository.TestResultRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class LabWorkflowService {
    private final LabOrderRepository labOrderRepository;
    private final TestResultRepository testResultRepository;
    private final LabResultEventProducer eventProducer;

    public LabWorkflowService(LabOrderRepository labOrderRepository, TestResultRepository testResultRepository, LabResultEventProducer eventProducer) {
        this.labOrderRepository = labOrderRepository;
        this.testResultRepository = testResultRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public void createFromPlacedEvent(LabOrderPlacedEvent event) {
        LabOrder order = new LabOrder();
        order.setOrderId(event.orderId);
        order.setDoctorId(event.doctorId);
        order.setPatientId(event.patientId);
        order.setRequestedAt(event.occurredAt != null ? event.occurredAt : Instant.now());
        order.setStatus(LabOrderStatus.QUEUED);
        order.setPriority("NORMAL");
        order.setTotalAmount(event.orderTotal);
        labOrderRepository.save(order);
    }

    @Transactional
    public LabOrder start(UUID orderId) {
        LabOrder order = labOrderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(LabOrderStatus.IN_PROGRESS);
        order.setStartedAt(Instant.now());
        return labOrderRepository.save(order);
    }

    @Transactional
    public LabOrder complete(UUID orderId, List<LabResultCompletedEvent.ResultItem> results, String reportUrl, String correlationId) {
        LabOrder order = labOrderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(LabOrderStatus.COMPLETED);
        order.setCompletedAt(Instant.now());
        LabOrder saved = labOrderRepository.save(order);

        for (LabResultCompletedEvent.ResultItem item : results) {
            TestResult result = new TestResult();
            result.setOrderId(orderId);
            result.setTestCode(item.testCode);
            result.setValue(item.value);
            result.setUnit(item.unit);
            result.setReferenceRange(item.referenceRange);
            result.setAbnormalFlag(item.abnormalFlag);
            result.setReportPdfUrl(reportUrl);
            result.setValidatedAt(Instant.now());
            testResultRepository.save(result);
        }

        eventProducer.publish(saved, results, reportUrl, correlationId);
        return saved;
    }
}
