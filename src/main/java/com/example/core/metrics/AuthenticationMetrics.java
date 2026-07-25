package com.example.core.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMetrics {

    private final Counter successfulAttempts;
    private final Counter failedAttempts;

    public AuthenticationMetrics(MeterRegistry meterRegistry) {
        successfulAttempts = Counter.builder("gym.authentication.attempts")
                .tag("result", "success")
                .register(meterRegistry);

        failedAttempts = Counter.builder("gym.authentication.attempts")
                .tag("result", "failure")
                .register(meterRegistry);
    }

    public void successfulAttempts() {
        successfulAttempts.increment();
    }

    public void failedAttempts() {
        failedAttempts.increment();
    }
}
