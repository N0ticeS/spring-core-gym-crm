package com.example.trainer_workload_service.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing() throws Exception {
        when(request.getHeader("Authorization"))
                .thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer() throws Exception {
        when(request.getHeader("Authorization"))
                .thenReturn("Basic test-token");

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws Exception {
        String token = "valid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.isTokenValid(token))
                .thenReturn(true);

        when(jwtService.extractUsername(token))
                .thenReturn("Mike.Johnson");

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertInstanceOf(
                UsernamePasswordAuthenticationToken.class,
                authentication
        );

        assertEquals(
                "Mike.Johnson",
                authentication.getPrincipal()
        );

        assertTrue(authentication.isAuthenticated());

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_USER"))
        );

        verify(jwtService)
                .isTokenValid(token);

        verify(jwtService)
                .extractUsername(token);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateUserWhenTokenIsInvalid() throws Exception {
        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.isTokenValid(token))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .isTokenValid(token);

        verify(jwtService, never())
                .extractUsername(anyString());

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldClearSecurityContextWhenJwtServiceThrowsException() throws Exception {
        String token = "broken-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.isTokenValid(token))
                .thenThrow(new RuntimeException("Invalid JWT"));

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "old-user",
                                null
                        )
                );

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }
}
