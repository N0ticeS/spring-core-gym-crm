package com.example.core.security.service;

import com.example.core.model.Role;
import com.example.core.model.User;
import com.example.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        var user = User.builder()
                .id(1L)
                .username("John.Smith")
                .password("encodedPassword")
                .role(Role.TRAINEE)
                .isActive(true)
                .build();

        when(userRepository.findByUsername("John.Smith"))
                .thenReturn(Optional.of(user));

        var result =
                customUserDetailsService.loadUserByUsername("John.Smith");

        assertNotNull(result);
        assertEquals("John.Smith", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.isEnabled());

        assertEquals(
                "ROLE_TRAINEE",
                result.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        verify(userRepository)
                .findByUsername("John.Smith");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUsername("Unknown.User"))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService
                        .loadUserByUsername("Unknown.User")
        );

        assertEquals(
                "User not found with username: Unknown.User",
                exception.getMessage()
        );

        verify(userRepository)
                .findByUsername("Unknown.User");
    }
}
