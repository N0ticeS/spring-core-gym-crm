package com.example.core.service.util;

import com.example.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernameGenerator {

    private static final String USERNAME_SEPARATOR = ".";

    private final UserRepository userRepository;

    public String generate(String firstName, String lastName) {
        var baseUsername = firstName + USERNAME_SEPARATOR + lastName;
        var username = baseUsername;
        int suffix = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
        }

        return username;
    }
}
