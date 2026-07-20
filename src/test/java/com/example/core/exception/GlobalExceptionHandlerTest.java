package com.example.core.exception;

import com.example.core.exception.auth.AuthenticationException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnNotFoundWhenEntityNotFoundExceptionThrown() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Entity not found"))
                .andExpect(jsonPath("$.path").value("/test/not-found"));
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthenticationExceptionThrown() throws Exception {
        mockMvc.perform(get("/test/authentication"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.path").value("/test/authentication"));
    }

    @Test
    void shouldReturnConflictWhenIllegalStateExceptionThrown() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Invalid operation"))
                .andExpect(jsonPath("$.path").value("/test/conflict"));
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedExceptionThrown() throws Exception {
        mockMvc.perform(get("/test/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Unexpected internal server error"))
                .andExpect(jsonPath("$.path").value("/test/internal-error"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        void throwEntityNotFoundException() {
            throw new EntityNotFoundException("Entity not found");
        }

        @GetMapping("/test/authentication")
        void throwAuthenticationException() {
            throw new AuthenticationException("Invalid username or password");
        }

        @GetMapping("/test/conflict")
        void throwIllegalStateException() {
            throw new IllegalStateException("Invalid operation");
        }

        @GetMapping("/test/internal-error")
        void throwUnexpectedException() {
            throw new RuntimeException("Unexpected error");
        }
    }
}
