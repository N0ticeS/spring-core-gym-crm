package com.example.core.service.impl;

import com.example.core.dto.auth.LoginRequestDto;
import com.example.core.exception.AuthenticationException;
import com.example.core.repository.UserRepository;
import com.example.core.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(LoginRequestDto request) {
        log.debug("Authenticating user with username {}", request.getUsername());

        var authenticated = userRepository.existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword());

        if (authenticated) {
            log.info("Authentication successful for username {}", request.getUsername());
        } else {
            log.warn("Authentication failed for username {}", request.getUsername());
        }

        return authenticated;
    }

    @Override
    @Transactional(readOnly = true)
    public void validateAuthentication(LoginRequestDto request) {
        log.debug("Validating authentication for username {}", request.getUsername());

        if (!authenticate(request)) {
            throw new AuthenticationException("Invalid username or password");
        }
    }
}
