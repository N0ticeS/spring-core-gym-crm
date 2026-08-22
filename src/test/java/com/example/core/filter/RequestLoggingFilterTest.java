package com.example.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RequestLoggingFilter requestLoggingFilter;

    @Test
    void shouldProcessRequestSuccessfully() throws Exception {
        when(request.getMethod())
                .thenReturn("POST");

        when(request.getRequestURI())
                .thenReturn("/api/trainings");

        when(response.getStatus())
                .thenReturn(200);

        requestLoggingFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verify(request, atLeastOnce())
                .getMethod();

        verify(request, atLeastOnce())
                .getRequestURI();

        verify(response)
                .getStatus();
    }

    @Test
    void shouldPropagateExceptionFromFilterChain() throws Exception {
        when(request.getMethod())
                .thenReturn("POST");

        when(request.getRequestURI())
                .thenReturn("/api/trainings");

        when(response.getStatus())
                .thenReturn(500);

        doThrow(new RuntimeException("Test exception"))
                .when(filterChain)
                .doFilter(request, response);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> requestLoggingFilter.doFilterInternal(
                        request,
                        response,
                        filterChain
                ),
                "RuntimeException should be propagated from filter chain"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                "Test exception",
                exception.getMessage(),
                "Exception message should match"
        );

        verify(filterChain)
                .doFilter(request, response);

        verify(response)
                .getStatus();
    }
}
