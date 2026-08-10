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
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    @InjectMocks
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Test
    void shouldReturnForbiddenErrorResponse() throws Exception {
        when(request.getRequestURI())
                .thenReturn("/api/trainings");

        when(response.getOutputStream())
                .thenReturn(outputStream);

        var exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(
                request,
                response,
                exception
        );

        verify(response).setStatus(HttpStatus.FORBIDDEN.value());

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        verify(response).setCharacterEncoding("UTF-8");

        verify(objectMapper).writeValue(
                eq(outputStream),
                argThat((ErrorResponseDto errorResponse) ->
                        errorResponse.getStatus() == 403
                                && "Forbidden".equals(errorResponse.getError())
                                && "You do not have permission to access this resource"
                                .equals(errorResponse.getMessage())
                                && "/api/trainings".equals(errorResponse.getPath())
                                && errorResponse.getTimestamp() != null
                )
        );
    }
}
