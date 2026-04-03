package com.project.lab_service.constants;

public class KafkaTopics {
    private KafkaTopics() {}

    public static final String LAB_ORDER_PLACED = "lab-order-placed.v1";
    public static final String LAB_RESULT_COMPLETED = "lab-result-completed.v1";
    public static final String LAB_ORDER_GROUP = "lab-order-group";
}
