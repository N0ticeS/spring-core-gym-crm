package com.example.core.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProfileCreationMetrics {

    private static final String METRIC_NAME = "gym.profiles.created";

    private final Counter traineeProfilesCreated;
    private final Counter trainerProfilesCreated;

    public ProfileCreationMetrics(MeterRegistry meterRegistry) {
        traineeProfilesCreated = Counter.builder(METRIC_NAME)
                .description("Number of successfully created profiles")
                .tag("type", "trainee")
                .register(meterRegistry);

        trainerProfilesCreated = Counter.builder(METRIC_NAME)
                .description("Number of successfully created profiles")
                .tag("type", "trainer")
                .register(meterRegistry);
    }

    public void recordTraineeCreated() {
        traineeProfilesCreated.increment();
    }

    public void recordTrainerCreated() {
        trainerProfilesCreated.increment();
    }
}
