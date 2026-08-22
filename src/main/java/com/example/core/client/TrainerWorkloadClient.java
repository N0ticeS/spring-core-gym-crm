package com.example.core.client;

import com.example.core.dto.workload.TrainerWorkloadRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class TrainerWorkloadClient {

    private final RestClient.Builder restClientBuilder;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final HttpServletRequest httpServletRequest;

    public TrainerWorkloadClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory,
            HttpServletRequest httpServletRequest
    ) {
        this.restClientBuilder = restClientBuilder;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.httpServletRequest = httpServletRequest;
    }

    public void updateWorkload(TrainerWorkloadRequestDto request) {
        var circuitBreaker = circuitBreakerFactory.create("trainerWorkloadService");

        var authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        var transactionId = MDC.get("transactionId");

        if (authorizationHeader == null) {
            throw new IllegalStateException("Missing Authorization header in request");
        }

        circuitBreaker.run(
                () -> {
                    restClientBuilder.build()
                            .post()
                            .uri("http://trainer-workload-service/api/v1/workloads")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                            .header("X-Transaction-Id", transactionId)
                            .body(request)
                            .retrieve()
                            .toBodilessEntity();

                    return null;
                },
                throwable -> {
                    log.error("Trainer workload service call failed for trainer {}",
                            request.getTrainerUsername(), throwable);

                    throw new IllegalStateException("Trainer workload service is currently unavailable", throwable);
                }
        );
    }
}
