package com.example.core.cucumber.steps;

import com.example.core.cucumber.context.TestContext;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class SecuritySteps {

    private final TestContext testContext;

    @Value("${local.server.port}")
    private int port;

    @When("the user requests all trainers without authentication")
    public void requestAllTrainersWithoutAuthentication() {
        try {
            var response = RestClient.create()
                    .get()
                    .uri("http://localhost:" + port + "/api/trainers")
                    .retrieve()
                    .toEntity(String.class);

            testContext.setStatus(response.getStatusCode());
            testContext.setResponseBody(response.getBody());

        } catch (HttpStatusCodeException ex) {
            testContext.setStatus(ex.getStatusCode());
            testContext.setResponseBody(ex.getResponseBodyAsString());
        }
    }

    @When("the authenticated user creates a trainee")
    public void authenticatedUserCreatesTrainee() {
        var body = """
                {
                  "firstName": "Test",
                  "lastName": "Trainee",
                  "dateOfBirth": "2000-01-01",
                  "address": "Test address"
                }
                """;

        try {
            var response = RestClient.create()
                    .post()
                    .uri("http://localhost:" + port + "/api/trainees")
                    .header(
                            "Authorization",
                            "Bearer " + testContext.getToken()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);

            testContext.setStatus(response.getStatusCode());
            testContext.setResponseBody(response.getBody());

        } catch (HttpStatusCodeException ex) {
            testContext.setStatus(ex.getStatusCode());
            testContext.setResponseBody(ex.getResponseBodyAsString());
        }
    }
}
