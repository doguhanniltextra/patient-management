package com.project.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemConsumedEvent {
    private String eventId;
    private String correlationId;
    private LocalDateTime occurredAt;
    
    private UUID patientId;
    private UUID itemId;
    private String itemCode;
    private Integer quantity;
    
    private BigDecimal unitPriceSnapshot;
    private String currency;
    
    private UUID prescribedBy;
}
