package com.example.core.security.handler;

import com.example.core.security.token.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtLogoutHandlerTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtLogoutHandler jwtLogoutHandler;

    @Test
    void shouldRevokeTokenSuccessfully() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("Bearer test-jwt-token");

        jwtLogoutHandler.logout(
                request,
                response,
                authentication
        );

        verify(tokenBlacklistService)
                .revoke("test-jwt-token");
    }

    @Test
    void shouldDoNothingWhenAuthorizationHeaderIsMissing() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn(null);

        jwtLogoutHandler.logout(
                request,
                response,
                authentication
        );

        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void shouldDoNothingWhenAuthorizationHeaderDoesNotContainBearerPrefix() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("test-jwt-token");

        jwtLogoutHandler.logout(
                request,
                response,
                authentication
        );

        verifyNoInteractions(tokenBlacklistService);
    }
}
