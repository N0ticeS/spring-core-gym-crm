package com.example.core.converter;

import com.example.core.config.properties.JwtProperties;
import com.example.core.dto.auth.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtToLoginResponseDtoConverter implements Converter<String, LoginResponseDto> {

    private final JwtProperties jwtProperties;

    @Override
    public LoginResponseDto convert(String token) {
        return LoginResponseDto.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtProperties.expiration() / 1000)
                .build();
    }
}
