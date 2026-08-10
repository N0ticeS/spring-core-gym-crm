package com.example.core.exception.auth;

import lombok.Getter;

import java.time.Instant;

@Getter
public class AccountTemporarilyLockedException extends RuntimeException {

    private final Instant lockedUntil;

    public AccountTemporarilyLockedException(
            String message,
            Instant lockedUntil
    ) {
        super(message);
        this.lockedUntil = lockedUntil;
    }
}
