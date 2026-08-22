package com.example.trainer_workload_service.exception;

import com.example.trainer_workload_service.dto.error.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TrainerWorkloadNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTrainerWorkloadNotFound(
            TrainerWorkloadNotFoundException exception,
            HttpServletRequest request
    ) {
        log.warn("Trainer workload not found: {}", exception.getMessage());

        return buildErrorResponse(
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(
            IllegalStateException exception,
            HttpServletRequest request
    ) {
        log.warn("Invalid workload operation: {}", exception.getMessage());

        return buildErrorResponse(
                exception.getMessage(),
                HttpStatus.CONFLICT,
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
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
            HttpServletRequest request
    ) {
        log.warn("Constraint validation failed: {}", exception.getMessage());

        return buildErrorResponse(
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        log.warn("Request body is invalid or unreadable");

        return buildErrorResponse(
                "Invalid request body",
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Missing request parameter: {}",
                exception.getParameterName()
        );

        return buildErrorResponse(
                "Required request parameter '" +
                        exception.getParameterName() +
                        "' is missing",
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception exception,
            HttpServletRequest request
    ) {
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
            HttpServletRequest request
    ) {
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
