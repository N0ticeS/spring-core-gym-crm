package com.example.core.security.bruteforce;

public interface LoginAttemptsService {
    void checkBlocked(String username);

    void loginSucceeded(String username);

    void loginFailed(String username);
}
