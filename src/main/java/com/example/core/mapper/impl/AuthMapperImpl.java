package com.example.core.mapper.impl;

import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.mapper.AuthMapper;
import com.example.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public CreatedProfileResponseDto toCreateProfileResponseDto(User user) {
        return CreatedProfileResponseDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
