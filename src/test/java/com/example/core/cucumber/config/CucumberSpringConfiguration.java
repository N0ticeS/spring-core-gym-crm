package com.example.core.cucumber.config;

import com.example.core.CoreApplication;
import com.example.core.messaging.producer.TrainerWorkloadProducer;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(
        classes =
                {
                        CoreApplication.class,
                        PostgresTestContainerConfiguration.class
                },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @MockitoBean
    private TrainerWorkloadProducer trainerWorkloadProducer;
}
