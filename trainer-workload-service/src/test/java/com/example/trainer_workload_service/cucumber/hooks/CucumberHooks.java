package com.example.trainer_workload_service.cucumber.hooks;

import com.example.trainer_workload_service.cucumber.context.TestContext;
import com.example.trainer_workload_service.repository.TrainerWorkloadRepository;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CucumberHooks {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final TestContext testContext;

    @Before
    public void beforeScenario() {
        trainerWorkloadRepository.deleteAll();
        testContext.clear();
    }
}
