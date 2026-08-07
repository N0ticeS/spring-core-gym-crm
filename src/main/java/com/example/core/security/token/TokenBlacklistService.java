package com.example.core.security.token;

public interface TokenBlacklistService {
    void revoke(String token);

    boolean isRevoked(String token);
}
