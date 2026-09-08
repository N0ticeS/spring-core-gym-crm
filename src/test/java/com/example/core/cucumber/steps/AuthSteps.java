package com.example.core.cucumber.steps;

import com.example.core.cucumber.context.TestContext;
import com.example.core.model.Role;
import com.example.core.model.User;
import com.example.core.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
public class AuthSteps {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    @Given("a user with username {string} and password {string} exists")
    public void userExists(String username, String password) {
        var user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.TRAINEE)
                .failedLoginAttempts(0)
                .isActive(true)
                .build();
        userRepository.save(user);
    }

    @When("the user logs in with username {string} and password {string}")
    public void login(String username, String password) throws Exception {
        var body = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        try {
            var response = RestClient.create()
                    .post()
                    .uri("http://localhost:" + port + "/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);

            testContext.setStatus(response.getStatusCode());
            testContext.setResponseBody(response.getBody());

            if (response.getBody() != null) {
                var json = objectMapper.readTree(response.getBody());

                if (json.has("token")) {
                    testContext.setToken(json.get("token").asText());
                }
            }

        } catch (HttpStatusCodeException ex) {
            testContext.setStatus(ex.getStatusCode());
            testContext.setResponseBody(ex.getResponseBodyAsString());
        }
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        assertNotNull(testContext.getStatus());
        assertEquals(expectedStatus, testContext.getStatus().value());
    }

    @Then("the response should contain a JWT token")
    public void responseShouldContainJwtToken() {
        assertNotNull(testContext.getToken());
        assertFalse(testContext.getToken().isBlank());
    }

    @Given("an admin user with username {string} and password {string} exists")
    public void adminUserExists(String username, String password) {
        var user = User.builder()
                .firstName("Admin")
                .lastName("User")
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .isActive(true)
                .failedLoginAttempts(0)
                .build();

        userRepository.save(user);
    }

    @Given("a trainer user with username {string} and password {string} exists")
    public void trainerUserExists(String username, String password) {
        var user = User.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.TRAINER)
                .isActive(true)
                .failedLoginAttempts(0)
                .build();

        userRepository.save(user);
    }
}
