package com.example.core.service.impl;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.LoginRequestDto;
import com.example.core.exception.auth.InvalidCredentialsException;
import com.example.core.metrics.AuthenticationMetrics;
import com.example.core.repository.UserRepository;
import com.example.core.security.bruteforce.LoginAttemptsService;
import com.example.core.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationMetrics authenticationMetrics;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptsService loginAttemptsService;

    @Override
    @Transactional(readOnly = true)
    public Authentication authenticate(LoginRequestDto request) {
        var username = request.getUsername();

        log.debug("Authenticating user with username {}", username);

        loginAttemptsService.checkBlocked(username);
        try {
            var authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                    username, request.getPassword());

            var authentication = authenticationManager.authenticate(authenticationRequest);

            loginAttemptsService.loginSucceeded(username);

            log.info("Authentication successful for username {}", request.getUsername());

            authenticationMetrics.successfulAttempts();

            return authentication;
        } catch (AuthenticationException e) {
            loginAttemptsService.loginFailed(username);
            log.warn("Authentication failed for username {}", request.getUsername());

            authenticationMetrics.failedAttempts();

            throw new InvalidCredentialsException(
                    "Invalid username or password"
            );
        }
    }

    @Override
    @Transactional
    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    public void changePassword(
            String username,
            ChangePasswordRequestDto request) {

        log.debug("Changing password for username {}", username);

        var loginRequest = LoginRequestDto.builder()
                .username(username)
                .password(request.getOldPassword())
                .build();

        authenticate(loginRequest);

        var user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User with username " + username + " not found"
                        )
                );

        var encodedPassword = passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);

        log.info("Password changed successfully for username {}", username);
    }
}
