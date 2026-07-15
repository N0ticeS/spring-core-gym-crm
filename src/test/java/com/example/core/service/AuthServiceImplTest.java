package com.example.core.service;

import com.example.core.dto.auth.LoginRequestDto;
import com.example.core.exception.AuthenticationException;
import com.example.core.repository.UserRepository;
import com.example.core.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldAuthenticateUserSuccessfully() {
        LoginRequestDto request = LoginRequestDto.builder()
                .username("John.Smith")
                .password("password123")
                .build();

        when(userRepository.existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword()))
                .thenReturn(true);

        boolean result = authService.authenticate(request);

        assertTrue(result, "User should be authenticated");

        verify(userRepository).existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword());
    }

    @Test
    void shouldReturnFalseWhenCredentialsAreInvalid() {
        LoginRequestDto request = LoginRequestDto.builder()
                .username("John.Smith")
                .password("wrongPassword")
                .build();

        when(userRepository.existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword()))
                .thenReturn(false);

        boolean result = authService.authenticate(request);

        assertFalse(result, "Authentication should fail");

        verify(userRepository).existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword());
    }

    @Test
    void shouldValidateAuthenticationSuccessfully() {
        LoginRequestDto request = LoginRequestDto.builder()
                .username("John.Smith")
                .password("password123")
                .build();

        when(userRepository.existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword()))
                .thenReturn(true);

        assertDoesNotThrow(() -> authService.validateAuthentication(request));

        verify(userRepository).existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword());
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenCredentialsAreInvalid() {
        LoginRequestDto request = LoginRequestDto.builder()
                .username("John.Smith")
                .password("wrongPassword")
                .build();

        when(userRepository.existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword()))
                .thenReturn(false);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.validateAuthentication(request)
        );

        assertEquals(
                "Invalid username or password",
                exception.getMessage(),
                "Exception message should match expected value"
        );

        verify(userRepository).existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword());
    }
}