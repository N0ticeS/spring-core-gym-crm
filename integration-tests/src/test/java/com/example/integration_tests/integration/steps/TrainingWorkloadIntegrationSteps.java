package com.example.integration_tests.integration.steps;

import com.example.integration_tests.integration.context.IntegrationTestContext;
import com.example.integration_tests.integration.hooks.IntegrationHooks;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.DriverManager;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TrainingWorkloadIntegrationSteps {

    private static final String CORE_URL =
            "http://localhost:8080";

    private static final String WORKLOAD_URL =
            "http://localhost:8081";

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Given("the Core and Workload services are running")
    public void servicesAreRunning() throws Exception {
        assertServiceAvailable(
                CORE_URL + "/actuator/health"
        );

        assertServiceAvailable(
                WORKLOAD_URL + "/actuator/health"
        );
    }

    @Given("trainer {string} and trainee {string} exist")
    public void trainerAndTraineeExist(String trainerUsername, String traineeUsername) {
        // They are loaded by Core sql fixtures.
        //
        // Mike.Johnson -> Trainer entity
        // John.Smith   -> Trainee entity
    }

    @Given("the user is authenticated as admin")
    public void authenticatedAsAdmin() throws Exception {
        var body = """
                {
                  "username": "Redis.Snow",
                  "password": "password123"
                }
                """;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(CORE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(
                200,
                response.statusCode(),
                response.body()
        );

        var json = objectMapper.readTree(response.body());

        IntegrationTestContext.setToken(
                json.get("token").asText()
        );
    }

    @When("a {int} minute training is created through Core")
    public void createTraining(int duration) throws Exception {
        var body = """
                {
                  "trainingName": "Integration Training",
                  "trainingDate": "%s",
                  "trainingDuration": %d,
                  "traineeUsername": "John.Smith",
                  "trainerUsername": "Mike.Johnson"
                }
                """.formatted(
                LocalDate.now().plusDays(1),
                duration
        );

        var request =
                HttpRequest.newBuilder()
                        .uri(URI.create(CORE_URL + "/api/trainings"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + IntegrationTestContext.getToken())
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        IntegrationTestContext.setResponseStatus(response.statusCode());

        IntegrationTestContext.setResponseBody(response.body());

        if (response.statusCode() == 200) {
            IntegrationTestContext.setTrainingId(findCreatedTrainingId());
        }
    }

    @Then("Core should return {int}")
    public void coreShouldReturn(int expectedStatus) {
        assertEquals(
                expectedStatus,
                IntegrationTestContext.getResponseStatus(),
                IntegrationTestContext.getResponseBody()
        );
    }

    @Then("eventually workload for {string} should contain {int} minutes")
    public void workloadShouldEventuallyContain(String username, int expectedDuration) throws Exception {

        var deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();

        while (System.nanoTime() < deadline) {
            var duration = requestWorkloadDuration(username);

            if (duration != null && duration == expectedDuration) {
                return;
            }

            Thread.sleep(250);
        }

        fail("Workload for " + username + " did not become " + expectedDuration + " within timeout");
    }

    @When("the created training is deleted through Core")
    public void deleteCreatedTraining() throws Exception {
        var trainingId =
                IntegrationTestContext.getTrainingId();

        if (trainingId == null) {
            throw new IllegalStateException(
                    "Training ID was not stored"
            );
        }

        var request =
                HttpRequest.newBuilder()
                        .uri(URI.create(CORE_URL + "/api/trainings/" + trainingId))
                        .header("Authorization", "Bearer " + IntegrationTestContext.getToken())
                        .DELETE()
                        .build();

        var response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        IntegrationTestContext.setResponseStatus(response.statusCode());

        IntegrationTestContext.setResponseBody(response.body());
    }

    @Then("workload for {string} should not exist")
    public void workloadShouldNotExist(String username) throws Exception {
        var date = LocalDate.now().plusDays(1);

        var url =
                WORKLOAD_URL
                        + "/api/v1/workloads/"
                        + username
                        + "?year="
                        + date.getYear()
                        + "&month="
                        + date.getMonthValue();

        var request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Authorization",
                                "Bearer "
                                        + IntegrationTestContext.getToken()
                        )
                        .GET()
                        .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(
                404,
                response.statusCode(),
                response.body()
        );
    }

    private Integer requestWorkloadDuration(String username) throws Exception {

        var date = LocalDate.now().plusDays(1);

        var url =
                WORKLOAD_URL
                        + "/api/v1/workloads/"
                        + username
                        + "?year="
                        + date.getYear()
                        + "&month="
                        + date.getMonthValue();

        var request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Authorization",
                                "Bearer "
                                        + IntegrationTestContext.getToken()
                        )
                        .GET()
                        .build();

        var response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return null;
        }

        var json = objectMapper.readTree(response.body());

        return json.get("trainingSummaryDuration").asInt();
    }

    private void assertServiceAvailable(String url) throws Exception {

        var request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    private Long findCreatedTrainingId() throws Exception {
        try (
                var connection =
                        DriverManager.getConnection(
                                IntegrationHooks.getPostgresJdbcUrl(),
                                IntegrationHooks.getPostgresUsername(),
                                IntegrationHooks.getPostgresPassword()
                        );

                var statement =
                        connection.prepareStatement("""
                                SELECT id
                                FROM trainings
                                WHERE training_name = ?
                                ORDER BY id DESC
                                LIMIT 1
                                """)
        ) {
            statement.setString(1, "Integration Training");

            try (var resultSet = statement.executeQuery()) {

                if (!resultSet.next()) {
                    throw new IllegalStateException(
                            "Created training was not found"
                    );
                }

                return resultSet.getLong("id");
            }
        }
    }
}
