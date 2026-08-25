package com.example.trainer_workload_service.messaging.consumer;

import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.service.TrainerWorkloadService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadConsumerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @Mock
    private Validator validator;

    @InjectMocks
    private TrainerWorkloadConsumer trainerWorkloadConsumer;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldConsumeValidMessageSuccessfully() {
        TrainerWorkloadRequestDto request = createRequest();

        when(validator.validate(request))
                .thenReturn(Collections.emptySet());

        trainerWorkloadConsumer.consume(
                request,
                "test-transaction-id"
        );

        verify(validator).validate(request);
        verify(trainerWorkloadService).updateWorkload(request);
    }

    @Test
    void shouldThrowExceptionWhenMessageIsInvalid() {
        TrainerWorkloadRequestDto request = createRequest();

        @SuppressWarnings("unchecked")
        ConstraintViolation<TrainerWorkloadRequestDto> violation =
                mock(ConstraintViolation.class);

        when(validator.validate(request))
                .thenReturn(Set.of(violation));

        assertThrows(
                ConstraintViolationException.class,
                () -> trainerWorkloadConsumer.consume(
                        request,
                        "test-transaction-id"
                )
        );

        verify(validator).validate(request);
        verifyNoInteractions(trainerWorkloadService);
    }

    @Test
    void shouldConsumeMessageWithoutTransactionId() {
        TrainerWorkloadRequestDto request = createRequest();

        when(validator.validate(request))
                .thenReturn(Collections.emptySet());

        trainerWorkloadConsumer.consume(request, null);

        verify(validator).validate(request);
        verify(trainerWorkloadService).updateWorkload(request);
    }

    private TrainerWorkloadRequestDto createRequest() {
        return TrainerWorkloadRequestDto.builder()
                .trainerUsername("Mike.Johnson")
                .trainerFirstName("Mike")
                .trainerLastName("Johnson")
                .active(true)
                .trainingDuration(60)
                .build();
    }
}
