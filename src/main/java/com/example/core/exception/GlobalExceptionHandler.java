package com.example.core.exception;

import com.example.core.dto.error.ErrorResponseDto;
import com.example.core.exception.auth.AuthenticationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(
            EntityNotFoundException exception,
            HttpServletRequest request) {

        log.warn("Entity not found: {}", exception.getMessage());

        return buildErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
            AuthenticationException exception,
            HttpServletRequest request) {

        log.warn("Authentication failed: {}", exception.getMessage());

        return buildErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(
            IllegalStateException exception,
            HttpServletRequest request) {

        log.warn("Operation conflict: {}", exception.getMessage());

        return buildErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        var message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", message);

        return buildErrorResponse(
                message,
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request) {

        log.warn("Constraint validation failed: {}", exception.getMessage());

        return buildErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        log.warn("Request body is invalid or unreadable");

        return buildErrorResponse(
                "Invalid request body",
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception exception,
            HttpServletRequest request) {

        log.error("Unexpected error occurred", exception);

        return buildErrorResponse(
                "Unexpected internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(
            String message,
            HttpStatus status,
            HttpServletRequest request) {

        var response = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
