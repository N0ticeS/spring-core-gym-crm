package com.example.trainer_workload_service.cucumber.steps;

import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.messaging.consumer.TrainerWorkloadConsumer;
import com.example.trainer_workload_service.model.ActionType;
import com.example.trainer_workload_service.repository.TrainerWorkloadRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class WorkloadConsumerSteps {

    private final TrainerWorkloadConsumer trainerWorkloadConsumer;
    private final TrainerWorkloadRepository trainerWorkloadRepository;

    private Exception thrownException;

    @When("the workload consumer receives {word} message for trainer {string} on {string} for {int} minutes")
    public void consumerReceivesMessage(String action, String username,
                                        String date, int duration) {
        var request = createRequest(
                username, LocalDate.parse(date),
                duration, ActionType.valueOf(action)
        );

        trainerWorkloadConsumer.consume(request, "cucumber-test-transaction");
    }

    @Then("workload for {string} year {int} month {int} should have duration {int}")
    public void workloadShouldHaveDuration(String username, int year, int month, int duration) {
        var workload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow();

        var yearSummary = workload.getYears().stream()
                .filter(summary -> summary.getYear().equals(year))
                .findFirst()
                .orElseThrow();

        var monthlySummary = yearSummary.getMonths().stream()
                .filter(summary -> summary.getMonth().equals(month))
                .findFirst()
                .orElseThrow();

        assertEquals(duration, monthlySummary.getTrainingSummaryDuration());
    }

    @When("the workload consumer receives invalid ADD message for trainer {string} with zero duration")
    public void consumerReceivesInvalidAddMessage(String username) {
        var request = createRequest(
                username, LocalDate.of(2026, 9, 10),
                0, ActionType.ADD);

        try {
            trainerWorkloadConsumer.consume(request, "cucumber-test-transaction");
        } catch (Exception ex) {
            thrownException = ex;
        }
    }

    @Then("a constraint violation should be thrown")
    public void constraintViolationShouldBeThrown() {
        assertNotNull(thrownException);

        assertInstanceOf(ConstraintViolationException.class, thrownException);
    }

    @Then("workload for trainer {string} should not exist")
    public void workloadShouldNotExists(String username) {
        assertTrue(trainerWorkloadRepository.findByUsername(username).isEmpty());
    }

    private TrainerWorkloadRequestDto createRequest(String username, LocalDate date,
                                                    int duration, ActionType actionType) {
        return TrainerWorkloadRequestDto.builder()
                .trainerUsername(username)
                .trainerFirstName("Mike")
                .trainerLastName("Johnson")
                .active(true)
                .trainingDate(date)
                .trainingDuration(duration)
                .actionType(actionType)
                .build();
    }
}
