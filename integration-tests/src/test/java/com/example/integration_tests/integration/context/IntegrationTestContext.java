package com.example.integration_tests.integration.context;

public class IntegrationTestContext {

    private static String token;
    private static String responseBody;
    private static int responseStatus;
    private static Long trainingId;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        IntegrationTestContext.token = token;
    }

    public static String getResponseBody() {
        return responseBody;
    }

    public static void setResponseBody(String responseBody) {
        IntegrationTestContext.responseBody = responseBody;
    }

    public static int getResponseStatus() {
        return responseStatus;
    }

    public static void setResponseStatus(int responseStatus) {
        IntegrationTestContext.responseStatus = responseStatus;
    }

    public static Long getTrainingId() {
        return trainingId;
    }

    public static void setTrainingId(Long trainingId) {
        IntegrationTestContext.trainingId = trainingId;
    }

    public static void clear() {
        token = null;
        responseBody = null;
        responseStatus = 0;
        trainingId = null;
    }
}
