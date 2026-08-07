package com.example.core.scheduler;

import com.example.core.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RevokedTokenCleanupScheduler {

    private final RevokedTokenRepository revokedTokenRepository;

    @Scheduled(fixedDelayString = "${security.token.cleanup-interval}")
    @Transactional
    public void removeExpiredTokens() {
        var currentTime = Instant.now();

        revokedTokenRepository.deleteByExpiresAtBefore(currentTime);

        log.debug("Expired revoked tokens cleanup completed at {}", currentTime);
    }
}
