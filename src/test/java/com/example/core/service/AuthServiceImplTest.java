package com.example.core.service;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.LoginRequestDto;
import com.example.core.exception.auth.InvalidCredentialsException;
import com.example.core.metrics.AuthenticationMetrics;
import com.example.core.model.User;
import com.example.core.repository.UserRepository;
import com.example.core.security.bruteforce.LoginAttemptsService;
import com.example.core.service.impl.AuthServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationMetrics authenticationMetrics;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LoginAttemptsService loginAttemptsService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldAuthenticateUserSuccessfully() {
        var request = LoginRequestDto.builder()
                .username("John.Smith")
                .password("password123")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        var result = authService.authenticate(request);

        assertSame(authentication, result);

        verify(loginAttemptsService)
                .checkBlocked("John.Smith");

        verify(authenticationManager)
                .authenticate(any());

        verify(loginAttemptsService)
                .loginSucceeded("John.Smith");

        verify(authenticationMetrics)
                .successfulAttempts();

        verify(loginAttemptsService, never())
                .loginFailed(anyString());

        verify(authenticationMetrics, never())
                .failedAttempts();
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenAuthenticationFails() {
        var request = LoginRequestDto.builder()
                .username("John.Smith")
                .password("wrongPassword")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        var exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate(request)
        );

        assertEquals(
                "Invalid username or password",
                exception.getMessage()
        );

        verify(loginAttemptsService)
                .checkBlocked("John.Smith");

        verify(loginAttemptsService)
                .loginFailed("John.Smith");

        verify(authenticationMetrics)
                .failedAttempts();

        verify(loginAttemptsService, never())
                .loginSucceeded(anyString());

        verify(authenticationMetrics, never())
                .successfulAttempts();
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        var username = "John.Smith";

        var request = ChangePasswordRequestDto.builder()
                .oldPassword("password123")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        var user = User.builder()
                .username(username)
                .password("encodedOldPassword")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn("encodedNewPassword");

        authService.changePassword(username, request);

        assertEquals(
                "encodedNewPassword",
                user.getPassword()
        );

        verify(loginAttemptsService).checkBlocked(username);

        verify(authenticationManager).authenticate(any());

        verify(loginAttemptsService).loginSucceeded(username);

        verify(userRepository).findByUsername(username);

        verify(passwordEncoder).encode("newPassword123");

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenOldPasswordIsInvalid() {
        var username = "John.Smith";

        var request = ChangePasswordRequestDto.builder()
                .oldPassword("wrongPassword")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        var exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.changePassword(username, request)
        );

        assertEquals(
                "Invalid username or password",
                exception.getMessage()
        );

        verify(loginAttemptsService)
                .checkBlocked(username);

        verify(loginAttemptsService)
                .loginFailed(username);

        verify(userRepository, never())
                .findByUsername(anyString());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        var username = "John.Smith";

        var request = ChangePasswordRequestDto.builder()
                .oldPassword("password123")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                EntityNotFoundException.class,
                () -> authService.changePassword(username, request)
        );

        assertEquals(
                "User with username John.Smith not found",
                exception.getMessage()
        );

        verify(loginAttemptsService)
                .checkBlocked(username);

        verify(authenticationManager)
                .authenticate(any());

        verify(loginAttemptsService)
                .loginSucceeded(username);

        verify(userRepository)
                .findByUsername(username);

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any());
    }
}
