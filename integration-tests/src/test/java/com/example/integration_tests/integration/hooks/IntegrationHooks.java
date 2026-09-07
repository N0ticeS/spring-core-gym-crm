package com.example.integration_tests.integration.hooks;

import com.example.integration_tests.integration.context.IntegrationTestContext;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.DriverManager;
import java.time.Duration;

public class IntegrationHooks {

    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:17-alpine")
            )
                    .withDatabaseName("gym_crm")
                    .withUsername("postgres")
                    .withPassword("root");
    private static final MongoDBContainer MONGO =
            new MongoDBContainer(
                    DockerImageName.parse("mongo:8.0")
            );
    private static final GenericContainer<?> ACTIVEMQ =
            new GenericContainer<>(
                    DockerImageName.parse("rmohr/activemq:latest")
            )
                    .withExposedPorts(61616);
    private static Process coreProcess;
    private static Process workloadProcess;

    public static String getPostgresJdbcUrl() {
        return POSTGRES.getJdbcUrl();
    }

    public static String getPostgresUsername() {
        return POSTGRES.getUsername();
    }

    public static String getPostgresPassword() {
        return POSTGRES.getPassword();
    }

    @BeforeAll
    public static void startEnvironment() throws Exception {
        POSTGRES.start();
        MONGO.start();
        ACTIVEMQ.start();

        var brokerUrl =
                "tcp://"
                        + ACTIVEMQ.getHost()
                        + ":"
                        + ACTIVEMQ.getMappedPort(61616);

        startCore(brokerUrl);
        startWorkload(brokerUrl);

        waitUntilAvailable(
                "http://localhost:8080/actuator/health",
                Duration.ofSeconds(60)
        );

        waitUntilAvailable(
                "http://localhost:8081/actuator/health",
                Duration.ofSeconds(60)
        );
    }

    @AfterAll
    public static void stopEnvironment() {
        if (coreProcess != null) {
            coreProcess.destroy();
        }

        if (workloadProcess != null) {
            workloadProcess.destroy();
        }

        ACTIVEMQ.stop();
        MONGO.stop();
        POSTGRES.stop();
    }

    private static void startCore(String brokerUrl) throws IOException {
        coreProcess = new ProcessBuilder(
                "java",
                "-jar",
                "../target/core-0.0.1-SNAPSHOT.jar",

                "--server.port=8080",

                "--spring.datasource.url=" + POSTGRES.getJdbcUrl(),
                "--spring.datasource.username=" + POSTGRES.getUsername(),
                "--spring.datasource.password=" + POSTGRES.getPassword(),

                "--spring.jpa.hibernate.ddl-auto=create",
                "--spring.sql.init.mode=always",

                "--spring.activemq.broker-url=" + brokerUrl,
                "--spring.activemq.user=admin",
                "--spring.activemq.password=admin",

                "--eureka.client.enabled=false",
                "--spring.cloud.discovery.enabled=false"
        )
                .inheritIO()
                .start();
    }

    private static void startWorkload(String brokerUrl) throws IOException {
        workloadProcess = new ProcessBuilder(
                "java",
                "-jar",
                "../trainer-workload-service/target/trainer-workload-service-0.0.1-SNAPSHOT.jar",

                "--server.port=8081",

                "--spring.mongodb.uri=" + MONGO.getReplicaSetUrl("trainer_workload_db"),

                "--spring.activemq.broker-url=" + brokerUrl,
                "--spring.activemq.user=admin",
                "--spring.activemq.password=admin",

                "--spring.jms.listener.auto-startup=true",

                "--eureka.client.enabled=false",
                "--spring.cloud.discovery.enabled=false"
        )
                .inheritIO()
                .start();
    }

    private static void waitUntilAvailable(String url, Duration timeout) throws Exception {

        var deadline = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {
            try {
                var connection =
                        (HttpURLConnection) URI.create(url)
                                .toURL()
                                .openConnection();

                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);

                if (connection.getResponseCode() == 200) {
                    return;
                }

            } catch (Exception ignored) {
            }

            Thread.sleep(500);
        }

        throw new IllegalStateException(
                "Service did not start in time: " + url
        );
    }

    private static void cleanMongo() throws Exception {
        var result = MONGO.execInContainer(
                "mongosh",
                "--quiet",
                "mongodb://localhost:27017/trainer_workload_db",
                "--eval",
                "db.trainer_workloads.deleteMany({})"
        );

        if (result.getExitCode() != 0) {
            throw new IllegalStateException(
                    "Failed to clean MongoDB: " + result.getStderr()
            );
        }
    }

    private static void cleanPostgres() throws Exception {
        try (
                var connection = DriverManager.getConnection(
                        POSTGRES.getJdbcUrl(),
                        POSTGRES.getUsername(),
                        POSTGRES.getPassword()
                );
                var statement = connection.createStatement()
        ) {
            statement.executeUpdate("DELETE FROM trainings");
        }
    }

    @Before
    public void beforeScenario() throws Exception {
        IntegrationTestContext.clear();

        cleanMongo();
        cleanPostgres();
    }
}
