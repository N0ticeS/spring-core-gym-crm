package com.example.core.mapper;

import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.model.User;

public interface AuthMapper {
    CreatedProfileResponseDto toCreateProfileResponseDto(User user);
}
