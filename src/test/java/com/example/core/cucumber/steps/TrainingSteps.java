package com.example.core.cucumber.steps;

import com.example.core.cucumber.context.TestContext;
import com.example.core.model.Training;
import com.example.core.repository.TraineeRepository;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class TrainingSteps {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TestContext testContext;

    @Value("${local.server.port}")
    private int port;

    @When("the authenticated user creates training {string} for trainee {string} with trainer {string} tomorrow for {int} minutes")
    public void createTrainingTomorrow(String name, String traineeUsername, String trainerUsername, int duration) {
        sendCreateTrainingRequest(
                name, traineeUsername, trainerUsername,
                LocalDate.now().plusDays(1), duration);
    }

    @When("the authenticated user creates training {string} for trainee {string} with trainer {string} yesterday for {int} minutes")
    public void createTrainingYesterday(String name, String traineeUsername, String trainerUsername, int duration) {
        sendCreateTrainingRequest(
                name, traineeUsername, trainerUsername,
                LocalDate.now().minusDays(1), duration);
    }

    @When("the authenticated user requests all trainings")
    public void requestAllTrainings() {
        try {
            var response = RestClient.create()
                    .get()
                    .uri(baseUrl() + "/api/trainings")
                    .header(
                            "Authorization",
                            "Bearer " + testContext.getToken()
                    )
                    .retrieve()
                    .toEntity(String.class);

            saveResponse(
                    response.getStatusCode(),
                    response.getBody()
            );

        } catch (HttpStatusCodeException ex) {
            saveResponse(
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );
        }
    }

    @Given("a future training {string} exists for trainee {string} with trainer {string}")
    public void futureTrainingExists(String name, String traineeUsername, String trainerUsername) {
        var training = saveTraining(
                name, traineeUsername, trainerUsername,
                LocalDate.now().plusDays(1), 60);

        testContext.setTrainingId(training.getId());
    }

    @Given("a past training {string} exists for trainee {string} with trainer {string}")
    public void pastTrainingExists(String name, String traineeUsername, String trainerUsername) {
        var training = saveTraining(
                name, traineeUsername, trainerUsername,
                LocalDate.now().minusDays(1), 60);

        testContext.setTrainingId(training.getId());
    }

    @When("the authenticated user deletes the saved training")
    public void deleteSavedTraining() {
        deleteTraining(testContext.getTrainingId());
    }

    @When("the authenticated user deletes training with id {long}")
    public void deleteTrainingWithId(long id) {
        deleteTraining(id);
    }

    @Then("training {string} should exist")
    public void trainingShouldExist(String name) {
        var exists = trainingRepository.findAll()
                .stream()
                .anyMatch(training ->
                        name.equals(training.getTrainingName())
                );

        assertTrue(exists, "Training not found: " + name);
    }

    @Then("the saved training should no longer exist")
    public void savedTrainingShouldNoLongerExist() {
        assertNotNull(testContext.getTrainingId());

        assertFalse(trainingRepository.existsById(testContext.getTrainingId()));
    }

    private void sendCreateTrainingRequest(String name, String traineeUsername,
                                           String trainerUsername, LocalDate date, int duration) {
        var body = """
                {
                  "trainingName": "%s",
                  "trainingDate": "%s",
                  "trainingDuration": %d,
                  "traineeUsername": "%s",
                  "trainerUsername": "%s"
                }
                """.formatted(name, date, duration, traineeUsername, trainerUsername);

        try {
            var response = RestClient.create()
                    .post()
                    .uri(baseUrl() + "/api/trainings")
                    .header(
                            "Authorization",
                            "Bearer " + testContext.getToken()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);

            saveResponse(
                    response.getStatusCode(),
                    response.getBody()
            );

        } catch (HttpStatusCodeException ex) {
            saveResponse(
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );
        }
    }

    private Training saveTraining(String name, String traineeUsername, String trainerUsername,
                                  LocalDate date, int duration) {
        var trainee = traineeRepository
                .findByUserUsername(traineeUsername)
                .orElseThrow();

        var trainer = trainerRepository
                .findByUserUsername(trainerUsername)
                .orElseThrow();

        var training = Training.builder()
                .trainingName(name)
                .trainingDate(date)
                .trainingDuration(duration)
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainer.getSpecialization())
                .build();

        return trainingRepository.save(training);
    }

    private void deleteTraining(long id) {
        try {
            var response = RestClient.create()
                    .delete()
                    .uri(baseUrl() + "/api/trainings/" + id)
                    .header(
                            "Authorization",
                            "Bearer " + testContext.getToken()
                    )
                    .retrieve()
                    .toEntity(String.class);

            saveResponse(
                    response.getStatusCode(),
                    response.getBody()
            );

        } catch (HttpStatusCodeException ex) {
            saveResponse(
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );
        }
    }

    private void saveResponse(org.springframework.http.HttpStatusCode status, String body) {
        testContext.setStatus(status);
        testContext.setResponseBody(body);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
