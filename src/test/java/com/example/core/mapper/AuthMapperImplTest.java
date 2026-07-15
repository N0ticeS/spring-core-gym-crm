package com.example.core.mapper;

import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.mapper.impl.AuthMapperImpl;
import com.example.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthMapperImplTest {

    private final AuthMapperImpl authMapper = new AuthMapperImpl();

    @Test
    void shouldMapUserToCreatedProfileResponseDto() {
        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("password123")
                .isActive(true)
                .build();

        CreatedProfileResponseDto result = authMapper.toCreateProfileResponseDto(user);

        assertNotNull(result, "Mapped DTO should not be null");

        assertEquals(
                user.getUsername(),
                result.getUsername(),
                "Username should be mapped correctly"
        );

        assertEquals(
                user.getPassword(),
                result.getPassword(),
                "Password should be mapped correctly"
        );
    }
}