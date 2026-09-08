package com.example.core.cucumber.steps;

import com.example.core.cucumber.context.TestContext;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class TrainerTrainingSteps {

    private final TestContext testContext;

    @Value("${local.server.port}")
    private int port;

    @When("the authenticated trainer {string} requests trainings")
    public void trainerRequestsTrainings(String username) {
        sendTrainingsRequest(username, null);
    }

    @When("the authenticated trainer {string} requests trainings from {string}")
    public void trainerRequestsTrainingsFrom(String username, String fromDate) {
        sendTrainingsRequest(username, fromDate);
    }

    private void sendTrainingsRequest(String username, String fromDate) {
        var uri = baseUrl()
                + "/api/trainers/"
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

            testContext.setStatus(
                    response.getStatusCode()
            );

            testContext.setResponseBody(
                    response.getBody()
            );

        } catch (HttpStatusCodeException ex) {
            testContext.setStatus(
                    ex.getStatusCode()
            );

            testContext.setResponseBody(
                    ex.getResponseBodyAsString()
            );
        }
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
