package com.example.core.service;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.LoginRequestDto;
import org.springframework.security.core.Authentication;

public interface AuthService {
    Authentication authenticate(LoginRequestDto request);

    void changePassword(String username, ChangePasswordRequestDto request);
}
