package com.project.billing_service.constants;

public class KafkaTopics {
    public static final String APPOINTMENT_PAYMENT_UPDATED = "appointment-payment-updated";
    public static final String APPOINTMENT_GROUP = "appointment-group";
    public static final String LAB_ORDER_PLACED = "${kafka.topics.lab-order-placed:lab-order-placed.v1}";
    public static final String LAB_ORDER_GROUP = "${kafka.groups.billing-lab-order:billing-lab-order-group}";
    public static final String INVENTORY_ITEM_CONSUMED = "${kafka.topics.inventory-item-consumed:inventory-item-consumed.v1}";
    public static final String ADMISSION_BED_CHARGE = "${kafka.topics.admission-bed-charge:admission-bed-charge.v1}";
    public static final String ADMISSION_DISCHARGED = "${kafka.topics.admission-discharged:admission-discharged.v1}";
}
