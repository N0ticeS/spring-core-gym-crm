package com.example.core.cucumber.hooks;

import com.example.core.cucumber.context.TestContext;
import com.example.core.repository.*;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CucumberHooks {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final TestContext testContext;

    @Before
    public void beforeScenario() {
        trainingRepository.deleteAll();
        traineeRepository.deleteAll();
        trainerRepository.deleteAll();
        trainingTypeRepository.deleteAll();
        userRepository.deleteAll();

        testContext.clear();
    }
}
