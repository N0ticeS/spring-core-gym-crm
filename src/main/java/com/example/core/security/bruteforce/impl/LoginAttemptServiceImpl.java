package com.example.core.security.bruteforce.impl;

import com.example.core.exception.auth.AccountTemporarilyLockedException;
import com.example.core.model.User;
import com.example.core.repository.UserRepository;
import com.example.core.security.bruteforce.LoginAttemptsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptServiceImpl implements LoginAttemptsService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(5);

    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkBlocked(String username) {
        var userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return;
        }

        var user = userOptional.get();
        var lockUntil = user.getLockedUntil();

        if (lockUntil == null) {
            return;
        }

        if (lockUntil.isAfter(Instant.now())) {
            log.warn("Authentication blocked for user {}, until {}", username, lockUntil);

            throw new AccountTemporarilyLockedException("Account is temporarily locked", lockUntil);
        }

        resetLoginAttempts(user);
        userRepository.save(user);

        log.info("Temporary authentication lock expired for user {}", username);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginSucceeded(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            if (user.getFailedLoginAttempts() == 0
                    && user.getLockedUntil() == null) {
                return;
            }

            resetLoginAttempts(user);
            userRepository.save(user);

            log.debug("Failed login attempts reset for username {}", username);
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void loginFailed(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            var failedAttempts = user.getFailedLoginAttempts() + 1;

            user.setFailedLoginAttempts(failedAttempts);

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                var lockUntil = Instant.now().plus(LOCK_DURATION);

                user.setLockedUntil(lockUntil);

                log.warn("Account temporarily locked for user {}, failed attempts {}, locked until {}", username, failedAttempts, lockUntil);
            } else {
                log.warn("Failed login attempt {} of {} for user {}", failedAttempts, MAX_FAILED_ATTEMPTS, username);
            }

            userRepository.save(user);
        });
    }

    private void resetLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
    }
}
