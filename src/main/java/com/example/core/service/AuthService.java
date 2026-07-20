package com.example.core.service;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.LoginRequestDto;

public interface AuthService {
    boolean authenticate(LoginRequestDto request);

    void validateAuthentication(LoginRequestDto request);

    void changePassword(String username, ChangePasswordRequestDto request);
}
