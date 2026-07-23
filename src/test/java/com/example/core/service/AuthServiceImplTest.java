package com.example.core.service;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.LoginRequestDto;
import com.example.core.exception.auth.AuthenticationException;
import com.example.core.model.User;
import com.example.core.repository.UserRepository;
import com.example.core.service.impl.AuthServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    @Test
    void shouldChangePasswordSuccessfully() {
        String username = "John.Smith";

        ChangePasswordRequestDto request = ChangePasswordRequestDto.builder()
                .oldPassword("password123")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        User user = User.builder()
                .username(username)
                .password("password123")
                .build();

        when(userRepository.existsByUsernameAndPassword(
                username,
                request.getOldPassword()))
                .thenReturn(true);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        authService.changePassword(username, request);

        assertEquals(
                request.getPassword(),
                user.getPassword(),
                "Password should be updated"
        );

        verify(userRepository).existsByUsernameAndPassword(
                username,
                request.getOldPassword());

        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenOldPasswordIsInvalid() {
        String username = "John.Smith";

        ChangePasswordRequestDto request = ChangePasswordRequestDto.builder()
                .oldPassword("wrongPassword")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userRepository.existsByUsernameAndPassword(
                username,
                request.getOldPassword()))
                .thenReturn(false);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.changePassword(username, request)
        );

        assertEquals(
                "Invalid username or password",
                exception.getMessage(),
                "Exception message should match expected value"
        );

        verify(userRepository).existsByUsernameAndPassword(
                username,
                request.getOldPassword());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        String username = "John.Smith";

        ChangePasswordRequestDto request = ChangePasswordRequestDto.builder()
                .oldPassword("password123")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userRepository.existsByUsernameAndPassword(
                username,
                request.getOldPassword()))
                .thenReturn(true);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> authService.changePassword(username, request)
        );

        assertEquals(
                "User with username John.Smith not found",
                exception.getMessage(),
                "Exception message should match expected value"
        );

        verify(userRepository).existsByUsernameAndPassword(
                username,
                request.getOldPassword());

        verify(userRepository).findByUsername(username);
    }
}
