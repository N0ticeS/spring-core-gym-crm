package com.example.core.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationMetricsTest {

    private SimpleMeterRegistry meterRegistry;
    private AuthenticationMetrics authenticationMetrics;

    @BeforeEach
    public void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        authenticationMetrics = new AuthenticationMetrics(meterRegistry);
    }

    @Test
    void successfulAttemptsShouldIncrementCounter() {
        authenticationMetrics.successfulAttempts();

        var counter = meterRegistry.get("gym.authentication.attempts")
                .tag("result", "success").counter();

        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void failedAttemptsShouldIncrementCounter() {
        authenticationMetrics.failedAttempts();

        var counter = meterRegistry.get("gym.authentication.attempts")
                .tag("result", "failure").counter();

        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    void shouldIncrementCountersWithMultipleTimes() {
        authenticationMetrics.successfulAttempts();
        authenticationMetrics.failedAttempts();
        authenticationMetrics.successfulAttempts();

        var successCounter = meterRegistry.get("gym.authentication.attempts")
                .tag("result", "success").counter();

        var failedCounter = meterRegistry.get("gym.authentication.attempts")
                .tag("result", "failure").counter();

        assertThat(successCounter.count()).isEqualTo(2.0);
        assertThat(failedCounter.count()).isEqualTo(1.0);
    }
}
