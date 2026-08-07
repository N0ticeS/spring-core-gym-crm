package com.example.core.repository;

import com.example.core.model.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByTokenId(String tokenId);

    void deleteByExpiresAtBefore(Instant expiresAt);
}
