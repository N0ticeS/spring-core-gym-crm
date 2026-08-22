package com.example.core.client;

import com.example.core.dto.workload.TrainerWorkloadRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadClientTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private TrainerWorkloadClient trainerWorkloadClient;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldUpdateWorkloadSuccessfully() {
        TrainerWorkloadRequestDto request = createRequest();

        String authorizationHeader = "Bearer test-token";
        String transactionId = "test-transaction-id";

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(authorizationHeader);

        MDC.put("transactionId", transactionId);

        when(circuitBreakerFactory.create("trainerWorkloadService"))
                .thenReturn(circuitBreaker);

        when(restClientBuilder.build())
                .thenReturn(restClient);

        when(restClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(
                "http://trainer-workload-service/api/v1/workloads"
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.header(
                HttpHeaders.AUTHORIZATION,
                authorizationHeader
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.header(
                "X-Transaction-Id",
                transactionId
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.body(request))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(circuitBreaker.run(
                any(Supplier.class),
                any(Function.class)
        )).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        trainerWorkloadClient.updateWorkload(request);

        verify(restClientBuilder).build();
        verify(restClient).post();

        verify(requestBodyUriSpec).uri(
                "http://trainer-workload-service/api/v1/workloads"
        );

        verify(requestBodySpec).contentType(
                MediaType.APPLICATION_JSON
        );

        verify(requestBodySpec).header(
                HttpHeaders.AUTHORIZATION,
                authorizationHeader
        );

        verify(requestBodySpec).header(
                "X-Transaction-Id",
                transactionId
        );

        verify(requestBodySpec).body(request);
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void shouldThrowExceptionWhenAuthorizationHeaderIsMissing() {
        TrainerWorkloadRequestDto request = createRequest();

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> trainerWorkloadClient.updateWorkload(request),
                "IllegalStateException should be thrown when Authorization header is missing"
        );

        assertEquals(
                "Missing Authorization header in request",
                exception.getMessage(),
                "Exception message should match"
        );

        verifyNoInteractions(restClientBuilder);
    }

    @Test
    void shouldThrowExceptionWhenCircuitBreakerFallbackIsTriggered() {
        TrainerWorkloadRequestDto request = createRequest();

        when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer test-token");

        when(circuitBreakerFactory.create("trainerWorkloadService"))
                .thenReturn(circuitBreaker);

        when(circuitBreaker.run(
                any(Supplier.class),
                any(Function.class)
        )).thenAnswer(invocation -> {
            Function<Throwable, ?> fallback =
                    invocation.getArgument(1);

            return fallback.apply(
                    new RuntimeException("Service unavailable")
            );
        });

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> trainerWorkloadClient.updateWorkload(request),
                "IllegalStateException should be thrown when workload service call fails"
        );

        assertEquals(
                "Trainer workload service is currently unavailable",
                exception.getMessage(),
                "Exception message should match"
        );
    }

    private TrainerWorkloadRequestDto createRequest() {
        return TrainerWorkloadRequestDto.builder()
                .trainerUsername("Mike.Brown")
                .trainingDuration(60)
                .build();
    }
}
