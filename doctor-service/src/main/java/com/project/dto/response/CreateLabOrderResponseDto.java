package com.project.dto.response;

import java.util.UUID;

public class CreateLabOrderResponseDto {
    private UUID orderId;
    private String status;

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
