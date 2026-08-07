package com.example.core.security.bruteforce;

import com.example.core.exception.auth.AccountTemporarilyLockedException;
import com.example.core.model.User;
import com.example.core.repository.UserRepository;
import com.example.core.security.bruteforce.impl.LoginAttemptServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    @Test
    void shouldDoNothingWhenUserDoesNotExistDuringBlockCheck() {
        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(
                () -> loginAttemptService.checkBlocked("John.Smith")
        );

        verify(userRepository).findByUsername("John.Smith");
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDoNothingWhenUserIsNotLocked() {
        var user = User.builder()
                .username("John.Smith")
                .failedLoginAttempts(1)
                .lockedUntil(null)
                .build();

        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.of(user));

        assertDoesNotThrow(
                () -> loginAttemptService.checkBlocked("John.Smith")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAccountIsStillLocked() {
        var lockedUntil = Instant.now().plusSeconds(60);

        var user = User.builder()
                .username("John.Smith")
                .failedLoginAttempts(3)
                .lockedUntil(lockedUntil)
                .build();

        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.of(user));

        var exception = assertThrows(
                AccountTemporarilyLockedException.class,
                () -> loginAttemptService.checkBlocked("John.Smith")
        );

        assertEquals(
                "Account is temporarily locked",
                exception.getMessage()
        );

        assertEquals(
                lockedUntil,
                exception.getLockedUntil()
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldResetLoginAttemptsWhenLockHasExpired() {
        var user = User.builder()
                .username("John.Smith")
                .failedLoginAttempts(3)
                .lockedUntil(Instant.now().minusSeconds(60))
                .build();

        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.of(user));

        loginAttemptService.checkBlocked("John.Smith");

        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getLockedUntil());

        verify(userRepository).save(user);
    }

    @Test
    void shouldResetLoginAttemptsAfterSuccessfulLogin() {
        var user = User.builder()
                .username("John.Smith")
                .failedLoginAttempts(2)
                .lockedUntil(null)
                .build();

        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.of(user));

        loginAttemptService.loginSucceeded("John.Smith");

        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getLockedUntil());

        verify(userRepository).save(user);
    }

    @Test
    void shouldDoNothingAfterSuccessfulLoginWhenAttemptsAlreadyReset() {
        var user = User.builder()
                .username("John.Smith")
                .failedLoginAttempts(0)
                .lockedUntil(null)
                .build();

        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.of(user));

        loginAttemptService.loginSucceeded("John.Smith");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldIncreaseFailedLoginAttempts() {
        var user = User.builder()
                .username("John.Smith")
                .failedLoginAttempts(1)
                .build();

        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.of(user));

        loginAttemptService.loginFailed("John.Smith");

        assertEquals(2, user.getFailedLoginAttempts());
        assertNull(user.getLockedUntil());

        verify(userRepository).save(user);
    }

    @Test
    void shouldLockAccountAfterThirdFailedLoginAttempt() {
        var user = User.builder()
                .username("John.Smith")
                .failedLoginAttempts(2)
                .build();

        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.of(user));

        var before = Instant.now();

        loginAttemptService.loginFailed("John.Smith");

        var after = Instant.now();

        assertEquals(3, user.getFailedLoginAttempts());
        assertNotNull(user.getLockedUntil());

        assertTrue(
                user.getLockedUntil()
                        .isAfter(before.plusSeconds(4 * 60))
        );

        assertTrue(
                user.getLockedUntil()
                        .isBefore(after.plusSeconds(6 * 60))
        );

        verify(userRepository).save(user);
    }

    @Test
    void shouldDoNothingWhenFailedLoginUserDoesNotExist() {
        when(userRepository.findByUsername("Unknown.User"))
                .thenReturn(Optional.empty());

        loginAttemptService.loginFailed("Unknown.User");

        verify(userRepository, never()).save(any());
    }
}
