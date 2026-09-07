package com.example.core.cucumber.steps;

import com.example.core.cucumber.context.TestContext;
import com.example.core.model.*;
import com.example.core.repository.TraineeRepository;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.repository.TrainingTypeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class TraineeTrainingSteps {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    @Given("a trainer with username {string} and specialization {string} exists")
    public void trainerExists(String username, String specialization) {
        var trainingType = trainingTypeRepository
                .findByTrainingTypeName(specialization)
                .orElseGet(() -> trainingTypeRepository.save(
                        TrainingType.builder()
                                .trainingTypeName(specialization)
                                .build()
                ));

        var names = username.split("\\.", 2);

        var user = User.builder()
                .firstName(names[0])
                .lastName(names.length > 1 ? names[1] : "Trainer")
                .username(username)
                .password(passwordEncoder.encode("Password123"))
                .role(Role.TRAINER)
                .isActive(true)
                .failedLoginAttempts(0)
                .build();

        var trainer = Trainer.builder()
                .user(user)
                .specialization(trainingType)
                .build();

        trainerRepository.save(trainer);
    }

    @Given("training {string} exists for trainee {string} with trainer {string} on {string} for {int} minutes")
    public void trainingExists(String trainingName, String traineeUsername, String trainerUsername,
                               String date, int duration) {
        var trainee = traineeRepository
                .findByUserUsername(traineeUsername)
                .orElseThrow();

        var trainer = trainerRepository
                .findByUserUsername(trainerUsername)
                .orElseThrow();

        var training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(trainingName)
                .trainingType(trainer.getSpecialization())
                .trainingDate(LocalDate.parse(date))
                .trainingDuration(duration)
                .build();

        trainingRepository.save(training);
    }

    @When("the authenticated trainee {string} requests trainings")
    public void requestsTrainings(String username) {
        sendTrainingsRequest(username, null);
    }

    @When("the authenticated trainee {string} requests trainings from {string}")
    public void requestsTrainingsFrom(String username, String fromDate) {
        sendTrainingsRequest(username, fromDate);
    }

    @Then("the response should contain {int} training")
    @Then("the response should contain {int} trainings")
    public void responseShouldContainTrainings(int expectedCount) throws Exception {
        var response = responseAsArray();

        assertEquals(
                expectedCount,
                response.size()
        );
    }

    @Then("the response should contain training {string}")
    public void responseShouldContainTraining(String trainingName) throws Exception {
        var response = responseAsArray();

        var found = false;

        for (var training : response) {
            if (trainingName.equals(training.get("trainingName").asText())) {
                found = true;
                break;
            }
        }

        assertTrue(
                found,
                "Training not found: " + trainingName
        );
    }

    private void sendTrainingsRequest(String username, String fromDate) {
        var uri = baseUrl()
                + "/api/trainees/"
                + username
                + "/trainings";

        if (fromDate != null) {
            uri += "?fromDate=" + fromDate;
        }

        try {
            var response = RestClient.create()
                    .get()
                    .uri(uri)
                    .header(
                            "Authorization",
                            "Bearer " + testContext.getToken()
                    )
                    .retrieve()
                    .toEntity(String.class);

            testContext.setStatus(response.getStatusCode());
            testContext.setResponseBody(response.getBody());

        } catch (HttpStatusCodeException ex) {
            testContext.setStatus(ex.getStatusCode());
            testContext.setResponseBody(ex.getResponseBodyAsString());
        }
    }

    private JsonNode responseAsArray() throws Exception {
        assertNotNull(testContext.getResponseBody());

        var response = objectMapper.readTree(
                testContext.getResponseBody()
        );

        assertTrue(
                response.isArray(),
                "Expected JSON array"
        );

        return response;
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
