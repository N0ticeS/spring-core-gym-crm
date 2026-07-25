package com.example.core.converter;

import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToCreatedProfileResponseDtoConverter implements Converter<User, CreatedProfileResponseDto> {

    @Override
    public CreatedProfileResponseDto convert(User source) {
        return CreatedProfileResponseDto.builder()
                .username(source.getUsername())
                .password(source.getPassword())
                .build();
    }
}
