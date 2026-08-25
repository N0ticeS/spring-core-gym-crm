package com.example.trainer_workload_service.config;

import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.boot.activemq.autoconfigure.ActiveMQConnectionFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

@Configuration
public class JmsConfig {

    @Bean
    public MessageConverter messageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();

        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        converter.setTypeIdMappings(Map.of(
                "trainerWorkload",
                TrainerWorkloadRequestDto.class
        ));

        return converter;
    }

    @Bean
    public ActiveMQConnectionFactoryCustomizer redeliveryCustomizer() {
        return connection -> {
            RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();

            redeliveryPolicy.setInitialRedeliveryDelay(1000);
            redeliveryPolicy.setRedeliveryDelay(1000);
            redeliveryPolicy.setMaximumRedeliveries(3);
            redeliveryPolicy.setUseExponentialBackOff(false);

            connection.setRedeliveryPolicy(redeliveryPolicy);
        };
    }
}