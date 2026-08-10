package com.example.core.converter;

import com.example.core.config.properties.JwtProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtToLoginResponseDtoConverterTest {

    @Test
    void shouldConvertJwtToLoginResponseDto() {
        var jwtProperties = mock(JwtProperties.class);

        when(jwtProperties.expiration()).thenReturn(3_600_000L);

        var converter = new JwtToLoginResponseDtoConverter(jwtProperties);

        var token = "test-jwt-token";

        var result = converter.convert(token);

        assertEquals(token, result.getToken(), "Token should match generated token");
        assertEquals("Bearer", result.getType(), "Type should match generated type");
        assertEquals(3600L, result.getExpiresIn(), "Expires in should match generated expires in");
    }
}
