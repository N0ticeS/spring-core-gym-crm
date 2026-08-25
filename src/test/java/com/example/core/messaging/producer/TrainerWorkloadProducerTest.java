package com.example.core.messaging.producer;

import com.example.core.config.properties.MessagingProperties;
import com.example.core.dto.workload.TrainerWorkloadRequestDto;
import jakarta.jms.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private MessagingProperties messagingProperties;

    @InjectMocks
    private TrainerWorkloadProducer trainerWorkloadProducer;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldSendWorkloadMessageWithTransactionId() throws Exception {
        TrainerWorkloadRequestDto request = createRequest();

        when(messagingProperties.trainerWorkloadQueue())
                .thenReturn("trainer.workload.queue");

        MDC.put("transactionId", "test-transaction-id");

        trainerWorkloadProducer.send(request);

        ArgumentCaptor<MessagePostProcessor> processorCaptor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        verify(jmsTemplate).convertAndSend(
                eq("trainer.workload.queue"),
                eq(request),
                processorCaptor.capture()
        );

        Message message = mock(Message.class);

        processorCaptor.getValue().postProcessMessage(message);

        verify(message).setStringProperty(
                "transactionId",
                "test-transaction-id"
        );
    }

    @Test
    void shouldSendWorkloadMessageWithoutTransactionId() throws Exception {
        TrainerWorkloadRequestDto request = createRequest();

        when(messagingProperties.trainerWorkloadQueue())
                .thenReturn("trainer.workload.queue");

        trainerWorkloadProducer.send(request);

        ArgumentCaptor<MessagePostProcessor> processorCaptor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        verify(jmsTemplate).convertAndSend(
                eq("trainer.workload.queue"),
                eq(request),
                processorCaptor.capture()
        );

        Message message = mock(Message.class);

        processorCaptor.getValue().postProcessMessage(message);

        verify(message, never()).setStringProperty(
                eq("transactionId"),
                anyString()
        );
    }

    private TrainerWorkloadRequestDto createRequest() {
        return TrainerWorkloadRequestDto.builder()
                .trainerUsername("Mike.Brown")
                .trainingDuration(60)
                .build();
    }
}
