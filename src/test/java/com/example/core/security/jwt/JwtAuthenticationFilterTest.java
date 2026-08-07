package com.example.core.security.jwt;

import com.example.core.security.service.CustomUserDetails;
import com.example.core.security.service.CustomUserDetailsService;
import com.example.core.security.token.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private CustomUserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing()
            throws Exception {

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(
                jwtService,
                customUserDetailsService,
                tokenBlacklistService
        );

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderHasNoBearerPrefix()
            throws Exception {

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("test-token");

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(
                jwtService,
                customUserDetailsService,
                tokenBlacklistService
        );
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsRevoked()
            throws Exception {

        var token = "revoked-token";

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(tokenBlacklistService.isRevoked(token))
                .thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(tokenBlacklistService).isRevoked(token);
        verify(filterChain).doFilter(request, response);

        verifyNoInteractions(
                jwtService,
                customUserDetailsService
        );
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid()
            throws Exception {

        var token = "valid-token";
        var username = "John.Smith";

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(tokenBlacklistService.isRevoked(token))
                .thenReturn(false);

        when(jwtService.extractUsername(token))
                .thenReturn(username);

        when(customUserDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userDetails))
                .thenReturn(true);

        when(userDetails.getAuthorities())
                .thenReturn(java.util.List.of());

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        var authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertSame(userDetails, authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());

        verify(customUserDetailsService)
                .loadUserByUsername(username);

        verify(jwtService)
                .isTokenValid(token, userDetails);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid()
            throws Exception {

        var token = "invalid-token";
        var username = "John.Smith";

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(tokenBlacklistService.isRevoked(token))
                .thenReturn(false);

        when(jwtService.extractUsername(token))
                .thenReturn(username);

        when(customUserDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(token, userDetails))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenJwtExceptionOccurs()
            throws Exception {

        var token = "expired-token";

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(tokenBlacklistService.isRevoked(token))
                .thenReturn(false);

        when(jwtService.extractUsername(token))
                .thenThrow(new JwtException("Token expired"));

        assertDoesNotThrow(() ->
                jwtAuthenticationFilter.doFilterInternal(
                        request,
                        response,
                        filterChain
                )
        );

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateAgainWhenSecurityContextAlreadyContainsAuthentication()
            throws Exception {

        var token = "valid-token";

        var existingAuthentication =
                mock(UsernamePasswordAuthenticationToken.class);

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer " + token);

        when(tokenBlacklistService.isRevoked(token))
                .thenReturn(false);

        when(jwtService.extractUsername(token))
                .thenReturn("John.Smith");

        jwtAuthenticationFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertSame(
                existingAuthentication,
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(customUserDetailsService);

        verify(jwtService, never())
                .isTokenValid(anyString(), any());

        verify(filterChain)
                .doFilter(request, response);
    }
}
