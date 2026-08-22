package com.example.trainer_workload_service.exception;

import com.example.trainer_workload_service.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleTrainerWorkloadNotFoundShouldReturn404() {
        var exception = new TrainerWorkloadNotFoundException(
                "Trainer workload not found"
        );

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads/Mike.Johnson");

        var response =
                globalExceptionHandler.handleTrainerWorkloadNotFound(
                        exception,
                        request
                );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponseDto body = response.getBody();

        assertNotNull(body);

        assertAll(
                () -> assertEquals(404, body.getStatus()),
                () -> assertEquals("Not Found", body.getError()),
                () -> assertEquals(
                        "Trainer workload not found",
                        body.getMessage()
                ),
                () -> assertEquals(
                        "/api/v1/workloads/Mike.Johnson",
                        body.getPath()
                ),
                () -> assertNotNull(body.getTimestamp())
        );
    }

    @Test
    void handleIllegalStateExceptionShouldReturn409() {
        var exception = new IllegalStateException(
                "Invalid workload operation"
        );

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads");

        var response =
                globalExceptionHandler.handleIllegalStateException(
                        exception,
                        request
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        var body = response.getBody();

        assertNotNull(body);

        assertAll(
                () -> assertEquals(409, body.getStatus()),
                () -> assertEquals("Conflict", body.getError()),
                () -> assertEquals(
                        "Invalid workload operation",
                        body.getMessage()
                ),
                () -> assertEquals(
                        "/api/v1/workloads",
                        body.getPath()
                )
        );
    }

    @Test
    void handleValidationExceptionShouldReturn400() {
        var exception = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(BindingResult.class);

        var usernameError = new FieldError(
                "trainerWorkloadRequestDto",
                "trainerUsername",
                "must not be blank"
        );

        var durationError = new FieldError(
                "trainerWorkloadRequestDto",
                "trainingDuration",
                "must be greater than or equal to 1"
        );

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(
                        usernameError,
                        durationError
                ));

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads");

        var response =
                globalExceptionHandler.handleValidationException(
                        exception,
                        request
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var body = response.getBody();

        assertNotNull(body);

        assertAll(
                () -> assertEquals(400, body.getStatus()),
                () -> assertEquals("Bad Request", body.getError()),
                () -> assertEquals(
                        "trainerUsername: must not be blank, "
                                + "trainingDuration: must be greater than or equal to 1",
                        body.getMessage()
                ),
                () -> assertEquals(
                        "/api/v1/workloads",
                        body.getPath()
                )
        );
    }

    @Test
    void handleConstraintViolationExceptionShouldReturn400() {
        var exception = mock(ConstraintViolationException.class);

        when(exception.getMessage())
                .thenReturn("month must be between 1 and 12");

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads/Mike.Johnson");

        var response =
                globalExceptionHandler.handleConstraintViolationException(
                        exception,
                        request
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var body = response.getBody();

        assertNotNull(body);

        assertAll(
                () -> assertEquals(400, body.getStatus()),
                () -> assertEquals("Bad Request", body.getError()),
                () -> assertEquals(
                        "month must be between 1 and 12",
                        body.getMessage()
                ),
                () -> assertEquals(
                        "/api/v1/workloads/Mike.Johnson",
                        body.getPath()
                )
        );
    }

    @Test
    void handleHttpMessageNotReadableExceptionShouldReturn400() {
        var exception = mock(HttpMessageNotReadableException.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads");

        var response =
                globalExceptionHandler.handleHttpMessageNotReadableException(
                        exception,
                        request
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        var body = response.getBody();

        assertNotNull(body);

        assertAll(
                () -> assertEquals(400, body.getStatus()),
                () -> assertEquals("Bad Request", body.getError()),
                () -> assertEquals(
                        "Invalid request body",
                        body.getMessage()
                ),
                () -> assertEquals(
                        "/api/v1/workloads",
                        body.getPath()
                )
        );
    }

    @Test
    void handleExceptionShouldReturn500() {
        var exception = new RuntimeException(
                "Database connection failed"
        );

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads");

        var response =
                globalExceptionHandler.handleException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        var body = response.getBody();

        assertNotNull(body);

        assertAll(
                () -> assertEquals(500, body.getStatus()),
                () -> assertEquals(
                        "Internal Server Error",
                        body.getError()
                ),
                () -> assertEquals(
                        "Unexpected internal server error",
                        body.getMessage()
                ),
                () -> assertEquals(
                        "/api/v1/workloads",
                        body.getPath()
                ),
                () -> assertNotNull(body.getTimestamp())
        );
    }

    @Test
    void handleMissingServletRequestParameterExceptionShouldReturn400() {
        var exception = new MissingServletRequestParameterException(
                "year",
                "Integer"
        );

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads/Mike.Johnson");

        var response =
                globalExceptionHandler.handleMissingServletRequestParameterException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        var body = response.getBody();

        assertNotNull(body);

        assertAll(
                () -> assertEquals(400, body.getStatus()),
                () -> assertEquals(
                        "Bad Request",
                        body.getError()
                ),
                () -> assertEquals(
                        "Required request parameter 'year' is missing",
                        body.getMessage()
                ),
                () -> assertEquals(
                        "/api/v1/workloads/Mike.Johnson",
                        body.getPath()
                ),
                () -> assertNotNull(body.getTimestamp())
        );
    }
}
