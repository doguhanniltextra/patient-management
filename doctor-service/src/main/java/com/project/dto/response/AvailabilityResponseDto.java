package com.project.dto.response;

public class AvailabilityResponseDto {
    private boolean available;
    private String reasonCode;
    private String message;

    public AvailabilityResponseDto() {
    }

    public AvailabilityResponseDto(boolean available, String reasonCode, String message) {
        this.available = available;
        this.reasonCode = reasonCode;
        this.message = message;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
