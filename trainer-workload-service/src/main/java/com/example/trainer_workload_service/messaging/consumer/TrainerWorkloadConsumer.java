package com.example.trainer_workload_service.messaging.consumer;

import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.service.TrainerWorkloadService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadConsumer {

    private final TrainerWorkloadService trainerWorkloadService;
    private final Validator validator;

    @JmsListener(
            destination = "${app.messaging.trainer-workload-queue}",
            concurrency = "2-5")
    public void consume(
            TrainerWorkloadRequestDto request,
            @Header(name = "transactionId", required = false) String transactionId
    ) {
        try {
            if (transactionId != null) {
                MDC.put("transactionId", transactionId);
            }

            log.info("Received trainer workload message. Trainer: {}, action: {}",
                    request.getTrainerUsername(), request.getActionType());

            var violations = validator.validate(request);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            trainerWorkloadService.updateWorkload(request);
        } finally {
            MDC.remove("transactionId");
        }
    }
}
