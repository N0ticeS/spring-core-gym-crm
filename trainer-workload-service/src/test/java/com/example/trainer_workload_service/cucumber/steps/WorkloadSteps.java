package com.example.trainer_workload_service.cucumber.steps;

import com.example.trainer_workload_service.config.properties.JwtProperties;
import com.example.trainer_workload_service.cucumber.context.TestContext;
import com.example.trainer_workload_service.model.MonthlySummary;
import com.example.trainer_workload_service.model.TrainerWorkload;
import com.example.trainer_workload_service.model.YearSummary;
import com.example.trainer_workload_service.repository.TrainerWorkloadRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class WorkloadSteps {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final JwtProperties jwtProperties;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;

    @Value("${local.server.port}")
    private int port;

    @Given("trainer workload for {string} exists for year {int} month {int} with duration {int}")
    public void trainerWorkloadExists(String username, int year, int month, int duration) {
        var monthlySummary = MonthlySummary.builder()
                .month(month)
                .trainingSummaryDuration(duration)
                .build();

        var yearSummary = YearSummary.builder()
                .year(year)
                .months(List.of(monthlySummary))
                .build();

        var workload = TrainerWorkload.builder()
                .username(username)
                .firstName("Mike")
                .lastName("Johnson")
                .active(true)
                .years(List.of(yearSummary))
                .build();

        trainerWorkloadRepository.save(workload);
    }

    @Given("a valid token for user {string} exists")
    public void validTokenExists(String username) {
        var keyBytes = Decoders.BASE64.decode(
                jwtProperties.secret()
        );

        var key = Keys.hmacShaKeyFor(keyBytes);

        var now = Instant.now();

        var token = Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key)
                .compact();

        testContext.setToken(token);
    }

    @When("the authenticated user requests workload for {string} year {int} month {int}")
    public void requestWorkload(String username, int year, int month) {
        var uri = baseUrl()
                + "/api/v1/workloads/"
                + username
                + "?year="
                + year
                + "&month="
                + month;

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
            testContext.setResponseBody(
                    ex.getResponseBodyAsString()
            );
        }
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, testContext.getStatus().value());
    }

    @Then("the workload response should contain duration {int}")
    public void responseShouldContainDuration(int expectedDuration) throws Exception {
        JsonNode json = objectMapper.readTree(
                testContext.getResponseBody()
        );

        assertEquals(
                expectedDuration,
                json.get("trainingSummaryDuration").asInt()
        );
    }

    @When("the unauthenticated user requests workload for {string} year {int} month {int}")
    public void unauthenticatedRequestWorkload(String username, int year, int month) {
        var uri = baseUrl()
                + "/api/v1/workloads/"
                + username
                + "?year="
                + year
                + "&month="
                + month;

        try {
            var response = RestClient.create()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(String.class);

            testContext.setStatus(response.getStatusCode());
            testContext.setResponseBody(response.getBody());

        } catch (HttpStatusCodeException ex) {
            testContext.setStatus(ex.getStatusCode());
            testContext.setResponseBody(
                    ex.getResponseBodyAsString()
            );
        }
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
