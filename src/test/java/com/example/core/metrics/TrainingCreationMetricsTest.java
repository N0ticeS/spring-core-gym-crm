package com.example.core.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingCreationMetricsTest {

    private SimpleMeterRegistry meterRegistry;
    private TrainingCreationMetrics trainingCreationMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        trainingCreationMetrics = new TrainingCreationMetrics(meterRegistry);
    }

    @Test
    void incrementShouldIncreaseCounter() {
        trainingCreationMetrics.increment();

        var counter = meterRegistry.get("gym.trainings.created")
                .counter();

        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void incrementShouldIncreaseCounterMultipleTimes() {
        trainingCreationMetrics.increment();
        trainingCreationMetrics.increment();
        trainingCreationMetrics.increment();

        var counter = meterRegistry.get("gym.trainings.created")
                .counter();

        assertThat(counter.count()).isEqualTo(3.0);
    }
}