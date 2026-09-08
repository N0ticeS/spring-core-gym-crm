package com.example.core.cucumber.steps;

import com.example.core.cucumber.context.TestContext;
import com.example.core.repository.TraineeRepository;
import com.example.core.repository.TrainerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class TraineeTrainerSteps {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    @Transactional
    @Given("trainer {string} is assigned to trainee {string}")
    public void trainerIsAssignedToTrainee(String trainerUsername, String traineeUsername) {
        var trainee = traineeRepository
                .findByUserUsername(traineeUsername)
                .orElseThrow();

        var trainer = trainerRepository
                .findByUserUsername(trainerUsername)
                .orElseThrow();

        trainee.getTrainers().add(trainer);

        traineeRepository.save(trainee);
    }

    @Transactional
    @When("the authenticated trainee {string} requests unassigned trainers")
    public void requestsUnassignedTrainers(String username) {
        try {
            var response = RestClient.create()
                    .get()
                    .uri(
                            baseUrl()
                                    + "/api/trainees/"
                                    + username
                                    + "/trainers/unassigned"
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

    @Transactional
    @When("the authenticated trainee {string} updates trainers to {string}")
    public void updatesTrainers(String username, String trainers) {
        var trainerUsernames = Arrays.stream(
                        trainers.split(",")
                )
                .map(String::trim)
                .toList();

        var jsonArray = trainerUsernames.stream()
                .map(value -> "\"" + value + "\"")
                .reduce((left, right) -> left + "," + right)
                .orElse("");

        var body = """
                {
                  "trainerUsernames": [%s]
                }
                """.formatted(jsonArray);

        try {
            var response = RestClient.create()
                    .put()
                    .uri(
                            baseUrl()
                                    + "/api/trainees/"
                                    + username
                                    + "/trainers"
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

    @Transactional
    @Then("the response should contain {int} trainer")
    @Then("the response should contain {int} trainers")
    public void responseShouldContainTrainers(int expectedCount) throws Exception {
        var response = responseAsArray();

        assertEquals(
                expectedCount,
                response.size()
        );
    }

    @Transactional
    @Then("the response should contain trainer {string}")
    public void responseShouldContainTrainer(String username) throws Exception {
        var response = responseAsArray();

        var found = false;

        for (var trainer : response) {
            if (username.equals(trainer.get("username").asText())) {
                found = true;
                break;
            }
        }

        assertTrue(
                found,
                "Trainer not found in response: " + username
        );
    }

    @Transactional
    @Then("the response should not contain trainer {string}")
    public void responseShouldNotContainTrainer(String username) throws Exception {
        var response = responseAsArray();

        for (var trainer : response) {
            assertNotEquals(
                    username,
                    trainer.get("username").asText()
            );
        }
    }

    @Transactional
    @Then("trainee {string} should have {int} assigned trainers")
    public void traineeShouldHaveAssignedTrainers(String username, int expectedCount) {
        var trainee = traineeRepository
                .findByUserUsername(username)
                .orElseThrow();

        assertEquals(
                expectedCount,
                trainee.getTrainers().size()
        );
    }

    @Transactional
    @Then("trainee {string} should have trainer {string}")
    public void traineeShouldHaveTrainer(String traineeUsername, String trainerUsername) {
        var trainee = traineeRepository
                .findByUserUsername(traineeUsername)
                .orElseThrow();

        var found = trainee.getTrainers()
                .stream()
                .anyMatch(
                        trainer -> trainerUsername.equals(
                                trainer.getUser().getUsername()
                        )
                );

        assertTrue(
                found,
                "Trainer is not assigned: " + trainerUsername
        );
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

    private void saveResponse(org.springframework.http.HttpStatusCode status, String body) {
        testContext.setStatus(status);
        testContext.setResponseBody(body);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
