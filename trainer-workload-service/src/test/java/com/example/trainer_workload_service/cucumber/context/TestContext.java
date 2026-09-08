package com.example.trainer_workload_service.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ScenarioScope
public class TestContext {

    private HttpStatusCode status;
    private String responseBody;
    private String token;

    public void clear() {
        status = null;
        responseBody = null;
        token = null;
    }
}
