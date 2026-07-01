package com.example.core.config;

import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class StorageConfig {

    @Bean("traineeStorage")
    public Map<Long, Trainee> traineeStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean("trainerStorage")
    public Map<Long, Trainer> trainerStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean("trainingStorage")
    public Map<Long, Training> trainingStorage() {
        return new ConcurrentHashMap<>();
    }
}
