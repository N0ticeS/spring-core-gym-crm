package com.example.core.security.service;

import com.example.core.model.Role;
import com.example.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void shouldCreateCustomUserDetailsFromUser() {
        var user = User.builder()
                .id(1L)
                .username("John.Smith")
                .password("encodedPassword")
                .role(Role.TRAINEE)
                .isActive(true)
                .build();

        var userDetails = new CustomUserDetails(user);

        assertEquals(1L, userDetails.getId());
        assertEquals("John.Smith", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        assertTrue(userDetails.isEnabled());

        assertEquals(
                1,
                userDetails.getAuthorities().size()
        );

        assertEquals(
                "ROLE_TRAINEE",
                userDetails.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );
    }

    @Test
    void shouldReturnDisabledWhenUserIsInactive() {
        var user = User.builder()
                .id(1L)
                .username("John.Smith")
                .password("encodedPassword")
                .role(Role.TRAINEE)
                .isActive(false)
                .build();

        var userDetails = new CustomUserDetails(user);

        assertFalse(userDetails.isEnabled());
    }
}
