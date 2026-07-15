package com.example.core.service;

import com.example.core.dto.auth.LoginRequestDto;

public interface AuthService {
    boolean authenticate(LoginRequestDto request);

    void validateAuthentication(LoginRequestDto request);
}
