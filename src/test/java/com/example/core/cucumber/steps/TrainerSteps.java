package com.example.core.cucumber.steps;

import com.example.core.cucumber.context.TestContext;
import com.example.core.model.TrainingType;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RequiredArgsConstructor
public class TrainerSteps {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    @Given("training type {string} exists")
    public void trainingTypeExists(String name) {
        trainingTypeRepository
                .findByTrainingTypeName(name)
                .orElseGet(() ->
                        trainingTypeRepository.save(
                                TrainingType.builder()
                                        .trainingTypeName(name)
                                        .build()
                        )
                );
    }

    @Given("an active trainer with username {string} and specialization {string} exists")
    public void activeTrainerExists(String username, String specialization) {
        trainerAlreadyExists(username, specialization);

        var trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow();

        trainer.getUser().setActive(true);

        trainerRepository.save(trainer);
    }

    @Given("a trainer with first name {string} last name {string} and specialization {string} is created")
    public void trainerIsCreated(String firstName, String lastName, String specialization) {
        sendCreateTrainerRequest(firstName, lastName, specialization);
    }

    @When("the authenticated user creates a trainer with first name {string} last name {string} and specialization {string}")
    public void createTrainer(String firstName, String lastName, String specialization) {
        sendCreateTrainerRequest(firstName, lastName, specialization);
    }

    @When("the authenticated user requests trainer {string}")
    public void requestTrainer(String username) {
        try {
            var response = RestClient.create()
                    .get()
                    .uri(
                            baseUrl()
                                    + "/api/trainers/"
                                    + username
                    )
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

    @When("the authenticated user updates trainer {string} with first name {string} and last name {string}")
    public void updateTrainer(String username, String firstName, String lastName) {
        var body = """
                {
                  "firstName": "%s",
                  "lastName": "%s"
                }
                """.formatted(firstName, lastName);

        try {
            var response = RestClient.create()
                    .put()
                    .uri(
                            baseUrl()
                                    + "/api/trainers/"
                                    + username
                    )
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

    @When("the authenticated user changes trainer {string} status to inactive")
    public void changeTrainerStatusToInactive(String username) {
        changeTrainerStatus(username, false);
    }

    @When("the authenticated user changes trainer {string} status to active")
    public void changeTrainerStatusToActive(String username) {
        changeTrainerStatus(username, true);
    }

    @Then("the response should contain trainer username {string}")
    public void responseShouldContainTrainerUsername(
            String expectedUsername
    ) throws Exception {
        var response = objectMapper.readTree(
                testContext.getResponseBody()
        );

        assertEquals(
                expectedUsername,
                response.get("username").asText()
        );
    }

    @Then("trainer {string} should be inactive")
    public void trainerShouldBeInactive(String username) {
        var trainer = trainerRepository
                .findByUserUsername(username)
                .orElseThrow();

        assertFalse(
                trainer.getUser().isActive()
        );
    }

    private void sendCreateTrainerRequest(String firstName, String lastName, String specialization) {
        var body = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "specialization": "%s"
                }
                """.formatted(firstName, lastName, specialization);

        try {
            var response = RestClient.create()
                    .post()
                    .uri(baseUrl() + "/api/trainers")
                    .header(
                            "Authorization",
                            "Bearer " + testContext.getToken()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);

            saveResponse(response.getStatusCode(), response.getBody());

        } catch (HttpStatusCodeException ex) {
            saveResponse(
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );
        }
    }

    private void changeTrainerStatus(String username, boolean active) {
        var body = """
                {
                  "active": %s
                }
                """.formatted(active);

        try {
            var response = RestClient.create()
                    .patch()
                    .uri(
                            baseUrl()
                                    + "/api/trainers/"
                                    + username
                                    + "/status"
                    )
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

    private void trainerAlreadyExists(String username, String specialization) {
        if (trainerRepository.existsByUserUsername(username)) {
            return;
        }

        throw new IllegalStateException(
                "Trainer must be created before this step: " + username
        );
    }

    private void saveResponse(org.springframework.http.HttpStatusCode status, String body) {
        testContext.setStatus(status);
        testContext.setResponseBody(body);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
