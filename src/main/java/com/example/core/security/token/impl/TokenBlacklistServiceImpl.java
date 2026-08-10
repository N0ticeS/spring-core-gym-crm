package com.example.core.security.token.impl;

import com.example.core.model.RevokedToken;
import com.example.core.repository.RevokedTokenRepository;
import com.example.core.security.jwt.JwtService;
import com.example.core.security.token.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void revoke(String token) {
        var tokenId = jwtService.extractTokenId(token);

        if (revokedTokenRepository.existsByTokenId(tokenId)) {
            log.debug("Token is already revoked, token ID: {}", tokenId);
            return;
        }

        var revokedToken = RevokedToken.builder()
                .tokenId(tokenId)
                .expiresAt(jwtService.extractExpiration(token))
                .build();

        revokedTokenRepository.save(revokedToken);
        log.info("Token revoked successfully, token ID: {}", tokenId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRevoked(String token) {
        var tokenId = jwtService.extractTokenId(token);

        return revokedTokenRepository.existsByTokenId(tokenId);
    }
}
