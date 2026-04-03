package com.project.billing_service.service;

import com.project.billing_service.client.ClaimClient;
import com.project.billing_service.dto.AppointmentDTO;
import com.project.billing_service.model.Claim;
import com.project.billing_service.model.ClaimStatus;
import com.project.billing_service.model.Invoice;
import com.project.billing_service.repository.ClaimRepository;
import com.project.billing_service.repository.InvoiceRepository;
import com.project.billing_service.strategy.InsuranceCalculationResult;
import com.project.billing_service.strategy.InsuranceFactory;
import com.project.billing_service.strategy.InsuranceStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingWorkflowServiceTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private InsuranceFactory insuranceFactory;

    @Mock
    private ClaimClient claimClient;

    @Mock
    private InsuranceStrategy insuranceStrategy;

    @InjectMocks
    private BillingWorkflowService billingWorkflowService;

    @Test
    public void processPaymentUpdate_WhenInsuranceAmountIsZero_DoesNotCreateClaim() {
        AppointmentDTO appointmentDTO = getAppointmentDTO();
        InsuranceCalculationResult split = new InsuranceCalculationResult(new BigDecimal("100.00"), BigDecimal.ZERO);

        when(insuranceFactory.getStrategy(anyString(), anyString())).thenReturn(insuranceStrategy);
        when(insuranceStrategy.calculate(any(BigDecimal.class))).thenReturn(split);
        when(invoiceService.generateInvoice(anyString(), anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(Path.of("invoices", "test.pdf"));

        billingWorkflowService.processPaymentUpdate(appointmentDTO);

        verify(invoiceRepository, times(1)).save(any(Invoice.class));
        verify(claimRepository, never()).save(any(Claim.class));
        verify(claimClient, never()).submitClaim(any());
    }

    @Test
    public void processPaymentUpdate_WhenInsuranceAmountExistsAndClaimFails_SavesPendingClaim() {
        AppointmentDTO appointmentDTO = getAppointmentDTO();
        InsuranceCalculationResult split = new InsuranceCalculationResult(new BigDecimal("20.00"), new BigDecimal("80.00"));

        when(insuranceFactory.getStrategy(anyString(), anyString())).thenReturn(insuranceStrategy);
        when(insuranceStrategy.calculate(any(BigDecimal.class))).thenReturn(split);
        when(invoiceService.generateInvoice(anyString(), anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(Path.of("invoices", "test.pdf"));
        when(claimClient.submitClaim(any())).thenReturn(false);

        billingWorkflowService.processPaymentUpdate(appointmentDTO);

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository, times(1)).save(captor.capture());

        Claim savedClaim = captor.getValue();
        assertThat(savedClaim.getStatus()).isEqualTo(ClaimStatus.PENDING);
        assertThat(savedClaim.getProviderName()).isEqualTo("Allianz");
    }

    @Test
    public void retryPendingClaims_WhenClaimSucceeds_UpdatesClaimAsApproved() {
        UUID invoiceId = UUID.randomUUID();
        Claim pendingClaim = new Claim();
        pendingClaim.setClaimId(UUID.randomUUID());
        pendingClaim.setInvoiceId(invoiceId);
        pendingClaim.setProviderName("SGK");
        pendingClaim.setStatus(ClaimStatus.PENDING);
        pendingClaim.setSubmittedAt(LocalDateTime.now().minusMinutes(10));

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(invoiceId);
        invoice.setInsuranceOwes(new BigDecimal("250.00"));

        when(claimRepository.findByStatus(ClaimStatus.PENDING)).thenReturn(List.of(pendingClaim));
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(claimClient.submitClaim(any())).thenReturn(true);

        billingWorkflowService.retryPendingClaims();

        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ClaimStatus.APPROVED);
    }

    private static AppointmentDTO getAppointmentDTO() {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setDoctorId(UUID.randomUUID().toString());
        appointmentDTO.setPatientId(UUID.randomUUID().toString());
        appointmentDTO.setAmount(100.00);
        appointmentDTO.setInsuranceProviderType("PRIVATE");
        appointmentDTO.setProviderName("Allianz");
        appointmentDTO.setPaymentStatus(true);
        return appointmentDTO;
    }
}
