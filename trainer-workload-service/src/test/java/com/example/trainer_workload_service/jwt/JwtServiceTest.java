package com.example.trainer_workload_service.security.jwt;

import com.example.trainer_workload_service.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET =
            "VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdKd3RTZWNyZXRLZXlGb3JIUzI1Ng==";

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void extractUsernameShouldReturnSubjectFromToken() {
        when(jwtProperties.secret())
                .thenReturn(SECRET);

        String token = createToken(
                "Mike.Johnson",
                new Date(System.currentTimeMillis() + 60_000)
        );

        String username = jwtService.extractUsername(token);

        assertEquals(
                "Mike.Johnson",
                username
        );
    }

    @Test
    void isTokenValidShouldReturnTrueWhenTokenIsNotExpired() {
        when(jwtProperties.secret())
                .thenReturn(SECRET);

        String token = createToken(
                "Mike.Johnson",
                new Date(System.currentTimeMillis() + 60_000)
        );

        boolean result = jwtService.isTokenValid(token);

        assertTrue(result);
    }

    @Test
    void isTokenValidShouldThrowExceptionWhenTokenIsExpired() {
        when(jwtProperties.secret())
                .thenReturn(SECRET);

        String token = createToken(
                "Mike.Johnson",
                new Date(System.currentTimeMillis() - 60_000)
        );

        assertThrows(
                RuntimeException.class,
                () -> jwtService.isTokenValid(token)
        );
    }

    @Test
    void extractUsernameShouldThrowExceptionWhenTokenHasInvalidSignature() {
        when(jwtProperties.secret())
                .thenReturn(SECRET);

        String otherSecret =
                "QW5vdGhlclN1ZmZpY2llbnRseUxvbmdKd3RTZWNyZXRLZXlGb3JIUzI1Ng==";

        String token = createToken(
                "Mike.Johnson",
                new Date(System.currentTimeMillis() + 60_000),
                otherSecret
        );

        assertThrows(
                RuntimeException.class,
                () -> jwtService.extractUsername(token)
        );
    }

    private String createToken(
            String username,
            Date expiration
    ) {
        return createToken(
                username,
                expiration,
                SECRET
        );
    }

    private String createToken(
            String username,
            Date expiration,
            String secret
    ) {
        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}
