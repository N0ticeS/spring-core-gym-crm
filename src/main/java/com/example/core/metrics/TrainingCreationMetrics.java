package com.example.core.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainingCreationMetrics {

    private final Counter createdTrainingsCounter;

    public TrainingCreationMetrics(MeterRegistry meterRegistry) {
        createdTrainingsCounter = Counter.builder(
                        "gym.trainings.created"
                )
                .description("Number of successfully created trainings")
                .register(meterRegistry);
    }

    public void increment() {
        createdTrainingsCounter.increment();
    }
}
