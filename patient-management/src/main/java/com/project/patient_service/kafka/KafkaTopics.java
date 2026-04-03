package com.project.patient_service.kafka;

public class KafkaTopics {
    private KafkaTopics() {}

    public static final String LAB_RESULT_COMPLETED = "${kafka.topics.lab-result-completed:lab-result-completed.v1}";
    public static final String PATIENT_LAB_RESULT_GROUP = "${kafka.groups.patient-lab-result:patient-lab-result-group}";
}
