package com.example.core.cucumber.steps;

import com.example.core.cucumber.context.TestContext;
import com.example.core.model.Role;
import com.example.core.model.Trainee;
import com.example.core.model.User;
import com.example.core.repository.TraineeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class TraineeSteps {

    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    @Given("a trainee with username {string} exists")
    public void traineeWithUsernameExists(String username) {
        createTraineeDirectly(username, true);
    }

    @Given("an active trainee with username {string} exists")
    public void activeTraineeWithUsernameExists(String username) {
        createTraineeDirectly(username, true);
    }

    @Given("a trainee with first name {string} and last name {string} is created")
    public void traineeWithNameIsCreated(String firstName, String lastName) {
        sendCreateTraineeRequest(
                firstName,
                lastName,
                LocalDate.of(2000, 1, 1),
                "Test address"
        );
    }

    @When("the authenticated user creates a trainee with first name {string} and last name {string}")
    public void authenticatedUserCreatesTrainee(String firstName, String lastName) {
        sendCreateTraineeRequest(
                firstName,
                lastName,
                LocalDate.of(2000, 1, 1),
                "Test address"
        );
    }

    @When("the authenticated user creates a trainee with future date of birth")
    public void authenticatedUserCreatesTraineeWithFutureDateOfBirth() {
        sendCreateTraineeRequest(
                "John",
                "Smith",
                LocalDate.now().plusYears(1),
                "Test address"
        );
    }

    @When("the authenticated user requests trainee {string}")
    public void authenticatedUserRequestsTrainee(String username) {
        try {
            var response = RestClient.create()
                    .get()
                    .uri(baseUrl() + "/api/trainees/" + username)
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

    @When("the authenticated user updates trainee {string} with first name {string} and last name {string}")
    public void authenticatedUserUpdatesTrainee(String username, String firstName, String lastName) {
        var body = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "dateOfBirth": "2000-01-01",
                  "address": "Updated address"
                }
                """.formatted(firstName, lastName);

        try {
            var response = RestClient.create()
                    .put()
                    .uri(baseUrl() + "/api/trainees/" + username)
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

    @When("the authenticated user changes trainee {string} status to inactive")
    public void authenticatedUserChangesTraineeStatusToInactive(String username) {
        sendChangeStatusRequest(username, false);
    }

    @When("the authenticated user changes trainee {string} status to active")
    public void authenticatedUserChangesTraineeStatusToActive(String username) {
        sendChangeStatusRequest(username, true);
    }

    @When("the authenticated user deletes trainee {string}")
    public void authenticatedUserDeletesTrainee(String username) {
        try {
            var response = RestClient.create()
                    .delete()
                    .uri(baseUrl() + "/api/trainees/" + username)
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

    @Then("the response should contain trainee username {string}")
    public void responseShouldContainTraineeUsername(String username) throws Exception {
        assertNotNull(testContext.getResponseBody());

        var json = objectMapper.readTree(testContext.getResponseBody());

        assertTrue(
                json.has("username"),
                "Response does not contain username"
        );

        assertEquals(
                username,
                json.get("username").asText()
        );
    }

    @Then("the response should contain first name {string}")
    public void responseShouldContainFirstName(String firstName) throws Exception {
        assertNotNull(testContext.getResponseBody());

        var json = objectMapper.readTree(testContext.getResponseBody());

        assertTrue(
                json.has("firstName"),
                "Response does not contain firstName"
        );

        assertEquals(
                firstName,
                json.get("firstName").asText()
        );
    }

    @Then("trainee {string} should be inactive")
    public void traineeShouldBeInactive(String username) {
        var trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow();

        assertFalse(trainee.getUser().isActive());
    }

    @Then("trainee {string} should no longer exist")
    public void traineeShouldNoLongerExist(String username) {
        assertFalse(
                traineeRepository.existsByUserUsername(username)
        );
    }

    private void sendCreateTraineeRequest(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        var body = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "dateOfBirth": "%s",
                  "address": "%s"
                }
                """.formatted(firstName, lastName, dateOfBirth, address);

        try {
            var response = RestClient.create()
                    .post()
                    .uri(baseUrl() + "/api/trainees")
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

    private void sendChangeStatusRequest(String username, boolean active) {
        var body = """
                {
                  "active": %s
                }
                """.formatted(active);

        try {
            var response = RestClient.create()
                    .patch()
                    .uri(baseUrl() + "/api/trainees/" + username + "/status")
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

    private void createTraineeDirectly(String username, boolean active) {
        var nameParts = username.split("\\.", 2);

        var firstName = nameParts.length > 0
                ? nameParts[0]
                : "John";

        var lastName = nameParts.length > 1
                ? nameParts[1]
                : "Smith";

        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(passwordEncoder.encode("Password123"))
                .role(Role.TRAINEE)
                .isActive(active)
                .failedLoginAttempts(0)
                .lockedUntil(null)
                .build();

        var trainee = Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Test address")
                .build();

        traineeRepository.save(trainee);
    }

    private void saveResponse(org.springframework.http.HttpStatusCode status, String responseBody) {
        testContext.setStatus(status);
        testContext.setResponseBody(responseBody);
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
