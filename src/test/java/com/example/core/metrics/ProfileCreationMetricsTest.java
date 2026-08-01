package com.example.core.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfileCreationMetricsTest {

    private SimpleMeterRegistry meterRegistry;
    private ProfileCreationMetrics profileCreationMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        profileCreationMetrics = new ProfileCreationMetrics(meterRegistry);
    }

    @Test
    void recordTraineeCreatedShouldIncrementCounter() {
        profileCreationMetrics.recordTraineeCreated();

        var counter = meterRegistry.get("gym.profiles.created")
                .tag("type", "trainee")
                .counter();

        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void recordTrainerCreatedShouldIncrementCounter() {
        profileCreationMetrics.recordTrainerCreated();

        var counter = meterRegistry.get("gym.profiles.created")
                .tag("type", "trainer")
                .counter();

        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void shouldIncrementCountersMultipleTimes() {
        profileCreationMetrics.recordTraineeCreated();
        profileCreationMetrics.recordTraineeCreated();
        profileCreationMetrics.recordTrainerCreated();

        var traineeCounter = meterRegistry.get("gym.profiles.created")
                .tag("type", "trainee")
                .counter();

        var trainerCounter = meterRegistry.get("gym.profiles.created")
                .tag("type", "trainer")
                .counter();

        assertThat(traineeCounter.count()).isEqualTo(2.0);
        assertThat(trainerCounter.count()).isEqualTo(1.0);
    }
}