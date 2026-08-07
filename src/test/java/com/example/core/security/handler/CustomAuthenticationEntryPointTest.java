package com.example.core.security.handler;

import com.example.core.dto.error.ErrorResponseDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    @Mock
    private AuthenticationException authenticationException;

    @InjectMocks
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    void shouldReturnUnauthorizedErrorResponse() throws Exception {
        when(request.getRequestURI())
                .thenReturn("/api/trainings");

        when(response.getOutputStream())
                .thenReturn(outputStream);

        authenticationEntryPoint.commence(
                request,
                response,
                authenticationException
        );

        verify(response)
                .setStatus(HttpStatus.UNAUTHORIZED.value());

        verify(response)
                .setContentType(MediaType.APPLICATION_JSON_VALUE);

        verify(response)
                .setCharacterEncoding("UTF-8");

        verify(objectMapper).writeValue(
                eq(outputStream),
                argThat((ErrorResponseDto errorResponse) ->
                        errorResponse.getStatus() == 401
                                && "Unauthorized".equals(errorResponse.getError())
                                && "Authentication is required to access this resource"
                                .equals(errorResponse.getMessage())
                                && "/api/trainings".equals(errorResponse.getPath())
                                && errorResponse.getTimestamp() != null
                )
        );
    }
}
