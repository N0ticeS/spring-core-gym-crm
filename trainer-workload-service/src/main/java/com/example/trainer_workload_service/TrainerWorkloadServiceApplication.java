package com.example.trainer_workload_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TrainerWorkloadServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainerWorkloadServiceApplication.class, args);
    }

}
