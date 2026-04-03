package com.project.lab_service;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LabServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LabServiceApplication.class, args);
    }
}
