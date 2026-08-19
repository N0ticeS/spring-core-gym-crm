package com.example.trainer_workload_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    private final RequestLoggingFilter requestLoggingFilter =
            new RequestLoggingFilter();
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Test
    void shouldProcessRequestAndContinueFilterChain() throws Exception {
        when(request.getMethod())
                .thenReturn("POST");

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads");

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
    void shouldLogGetRequestAndContinueFilterChain() throws Exception {
        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/api/v1/workloads/Mike.Johnson");

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
                .thenReturn("/api/v1/workloads");

        when(response.getStatus())
                .thenReturn(500);

        doThrow(new RuntimeException("Test exception"))
                .when(filterChain)
                .doFilter(request, response);

        assertThrows(
                RuntimeException.class,
                () -> requestLoggingFilter.doFilterInternal(
                        request,
                        response,
                        filterChain
                )
        );

        verify(filterChain)
                .doFilter(request, response);

        verify(response)
                .getStatus();
    }
}
