package com.example.core.security.token;

import com.example.core.model.RevokedToken;
import com.example.core.repository.RevokedTokenRepository;
import com.example.core.security.jwt.JwtService;
import com.example.core.security.token.impl.TokenBlacklistServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlackListServiceImplTest {

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Test
    void shouldRevokeTokenSuccessfully() {
        var token = "test-jwt-token";
        var tokenId = "test-token-id";
        var expiration = Instant.now().plusSeconds(3600);

        when(jwtService.extractTokenId(token))
                .thenReturn(tokenId);

        when(revokedTokenRepository.existsByTokenId(tokenId))
                .thenReturn(false);

        when(jwtService.extractExpiration(token))
                .thenReturn(expiration);

        tokenBlacklistService.revoke(token);

        var captor = ArgumentCaptor.forClass(RevokedToken.class);

        verify(revokedTokenRepository)
                .save(captor.capture());

        var revokedToken = captor.getValue();

        assertEquals(tokenId, revokedToken.getTokenId());
        assertEquals(expiration, revokedToken.getExpiresAt());

        verify(jwtService).extractTokenId(token);
        verify(jwtService).extractExpiration(token);
        verify(revokedTokenRepository).existsByTokenId(tokenId);
    }

    @Test
    void shouldNotRevokeTokenWhenTokenIsAlreadyRevoked() {
        var token = "test-jwt-token";
        var tokenId = "test-token-id";

        when(jwtService.extractTokenId(token))
                .thenReturn(tokenId);

        when(revokedTokenRepository.existsByTokenId(tokenId))
                .thenReturn(true);

        tokenBlacklistService.revoke(token);

        verify(jwtService).extractTokenId(token);
        verify(revokedTokenRepository).existsByTokenId(tokenId);

        verify(jwtService, never())
                .extractExpiration(anyString());

        verify(revokedTokenRepository, never())
                .save(any());
    }

    @Test
    void shouldReturnTrueWhenTokenIsRevoked() {
        var token = "test-jwt-token";
        var tokenId = "test-token-id";

        when(jwtService.extractTokenId(token))
                .thenReturn(tokenId);

        when(revokedTokenRepository.existsByTokenId(tokenId))
                .thenReturn(true);

        var result = tokenBlacklistService.isRevoked(token);

        assertTrue(result);

        verify(jwtService).extractTokenId(token);
        verify(revokedTokenRepository).existsByTokenId(tokenId);
    }

    @Test
    void shouldReturnFalseWhenTokenIsNotRevoked() {
        var token = "test-jwt-token";
        var tokenId = "test-token-id";

        when(jwtService.extractTokenId(token))
                .thenReturn(tokenId);

        when(revokedTokenRepository.existsByTokenId(tokenId))
                .thenReturn(false);

        var result = tokenBlacklistService.isRevoked(token);

        assertFalse(result);

        verify(jwtService).extractTokenId(token);
        verify(revokedTokenRepository).existsByTokenId(tokenId);
    }
}