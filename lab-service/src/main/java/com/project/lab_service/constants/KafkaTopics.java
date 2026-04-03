package com.project.lab_service.constants;

public class KafkaTopics {
    private KafkaTopics() {}

    public static final String LAB_ORDER_PLACED = "${kafka.topics.lab-order-placed:lab-order-placed.v1}";
    public static final String LAB_RESULT_COMPLETED = "${kafka.topics.lab-result-completed:lab-result-completed.v1}";
    public static final String LAB_ORDER_GROUP = "${kafka.groups.lab-order:lab-order-group}";
}
