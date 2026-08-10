package com.example.core.security.jwt;

import com.example.core.config.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        var jwtProperties = new JwtProperties(
                "VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdKd3RTZWNyZXRLZXlGb3JIUzI1Ng==",
                3_600_000L
        );

        jwtService = new JwtService(jwtProperties);

        userDetails = User.builder()
                .username("John.Smith")
                .password("password")
                .authorities("ROLE_TRAINEE")
                .build();
    }

    @Test
    void shouldGenerateTokenSuccessfully() {
        var token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(
                "John.Smith",
                jwtService.extractUsername(token)
        );
    }

    @Test
    void shouldExtractUsernameSuccessfully() {
        var token = jwtService.generateToken(userDetails);

        var username = jwtService.extractUsername(token);

        assertEquals(
                "John.Smith",
                username
        );
    }

    @Test
    void shouldExtractTokenIdSuccessfully() {
        var token = jwtService.generateToken(userDetails);

        var tokenId = jwtService.extractTokenId(token);

        assertNotNull(tokenId);
        assertFalse(tokenId.isBlank());
    }

    @Test
    void shouldGenerateUniqueTokenIds() {
        var firstToken = jwtService.generateToken(userDetails);
        var secondToken = jwtService.generateToken(userDetails);

        var firstTokenId =
                jwtService.extractTokenId(firstToken);

        var secondTokenId =
                jwtService.extractTokenId(secondToken);

        assertNotEquals(
                firstTokenId,
                secondTokenId
        );
    }

    @Test
    void shouldExtractExpirationSuccessfully() {
        var beforeGeneration = Instant.now();

        var token = jwtService.generateToken(userDetails);

        var expiration =
                jwtService.extractExpiration(token);

        var afterGeneration = Instant.now();

        assertTrue(
                expiration.isAfter(
                        beforeGeneration.plusSeconds(3590)
                )
        );

        assertTrue(
                expiration.isBefore(
                        afterGeneration.plusSeconds(3610)
                )
        );
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        var token = jwtService.generateToken(userDetails);

        var result =
                jwtService.isTokenValid(token, userDetails);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenTokenBelongsToAnotherUser() {
        var token = jwtService.generateToken(userDetails);

        var anotherUser = User.builder()
                .username("Mike.Brown")
                .password("password")
                .authorities("ROLE_TRAINER")
                .build();

        var result =
                jwtService.isTokenValid(token, anotherUser);

        assertFalse(result);
    }

    @Test
    void shouldThrowExceptionWhenTokenHasInvalidSignature() {
        var token = jwtService.generateToken(userDetails);

        var otherProperties = new JwtProperties(
                "QW5vdGhlclN1ZmZpY2llbnRseUxvbmdTZWNyZXRLZXlGb3JIUzI1Ng==",
                3_600_000L
        );

        var otherJwtService =
                new JwtService(otherProperties);

        assertThrows(
                RuntimeException.class,
                () -> otherJwtService.extractUsername(token)
        );
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {
        var shortLivedProperties = new JwtProperties(
                "VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdKd3RTZWNyZXRLZXlGb3JIUzI1Ng==",
                5L
        );

        var shortLivedJwtService =
                new JwtService(shortLivedProperties);

        var token =
                shortLivedJwtService.generateToken(userDetails);

        Thread.sleep(20);

        assertThrows(
                RuntimeException.class,
                () -> shortLivedJwtService.isTokenValid(
                        token,
                        userDetails
                )
        );
    }
}