package com.example.core.messaging.producer;

import com.example.core.config.properties.MessagingProperties;
import com.example.core.dto.workload.TrainerWorkloadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadProducer {

    private final JmsTemplate jmsTemplate;
    private final MessagingProperties messagingProperties;

    public void send(TrainerWorkloadRequestDto request) {
        log.info("Sending trainer workload message. Trainer: {}, action: {}",
                request.getTrainerUsername(), request.getActionType());

        jmsTemplate.convertAndSend(
                messagingProperties.trainerWorkloadQueue(),
                request,
                message -> {
                    String transactionId = MDC.get("transactionId");

                    if (transactionId != null) {
                        message.setStringProperty("transactionId", transactionId);
                    }
                    return message;

                }
        );
    }
}
