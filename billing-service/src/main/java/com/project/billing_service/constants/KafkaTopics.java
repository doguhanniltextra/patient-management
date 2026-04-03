package com.project.billing_service.constants;

public class KafkaTopics {
    public static final String APPOINTMENT_PAYMENT_UPDATED = "appointment-payment-updated";
    public static final String APPOINTMENT_GROUP = "appointment-group";
    public static final String LAB_ORDER_PLACED = "${kafka.topics.lab-order-placed:lab-order-placed.v1}";
    public static final String LAB_ORDER_GROUP = "${kafka.groups.billing-lab-order:billing-lab-order-group}";

}
