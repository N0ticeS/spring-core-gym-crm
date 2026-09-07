package com.example.core.cucumber.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class TestContext {

    private HttpStatusCode status;
    private String responseBody;
    private String token;
    private Long trainingId;

    public void clear() {
        status = null;
        responseBody = null;
        token = null;
    }
}
