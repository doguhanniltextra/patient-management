package com.project.inventory_service.model;

public enum TransactionType {
    REPLENISHMENT, // + Stock from Supplier
    CONSUMPTION,   // - Stock by Patient usage
    ADJUSTMENT,    // Manual Correction
    EXPIRED        // - Stock due to expiry
}
