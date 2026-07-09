package com.example.core.utils;

import com.example.core.repository.UserRepository;
import com.example.core.service.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsernameGenerator usernameGenerator;

    @Test
    void shouldGenerateUsernameWithoutSuffixWhenUsernameDoesNotExist() {
        when(userRepository.existsByUsername("John.Smith")).thenReturn(false);

        String username = usernameGenerator.generate("John", "Smith");

        assertEquals(
                "John.Smith",
                username,
                "Username should be generated without suffix"
        );

        verify(userRepository).existsByUsername("John.Smith");
    }

    @Test
    void shouldGenerateUsernameWithSuffixWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("John.Smith")).thenReturn(true);
        when(userRepository.existsByUsername("John.Smith1")).thenReturn(false);

        String username = usernameGenerator.generate("John", "Smith");

        assertEquals(
                "John.Smith1",
                username,
                "Username should have suffix 1 when base username already exists"
        );

        verify(userRepository).existsByUsername("John.Smith");
        verify(userRepository).existsByUsername("John.Smith1");
    }

    @Test
    void shouldGenerateUsernameWithNextAvailableSuffix() {
        when(userRepository.existsByUsername("John.Smith")).thenReturn(true);
        when(userRepository.existsByUsername("John.Smith1")).thenReturn(true);
        when(userRepository.existsByUsername("John.Smith2")).thenReturn(true);
        when(userRepository.existsByUsername("John.Smith3")).thenReturn(false);

        String username = usernameGenerator.generate("John", "Smith");

        assertEquals(
                "John.Smith3",
                username,
                "Username should have next available suffix"
        );

        verify(userRepository).existsByUsername("John.Smith");
        verify(userRepository).existsByUsername("John.Smith1");
        verify(userRepository).existsByUsername("John.Smith2");
        verify(userRepository).existsByUsername("John.Smith3");
    }

    @Test
    void shouldGenerateDifferentUsernamesForDifferentNames() {
        when(userRepository.existsByUsername("John.Smith")).thenReturn(false);
        when(userRepository.existsByUsername("Mike.Brown")).thenReturn(false);

        String firstUsername = usernameGenerator.generate("John", "Smith");
        String secondUsername = usernameGenerator.generate("Mike", "Brown");

        assertNotEquals(
                firstUsername,
                secondUsername,
                "Generated usernames should be different"
        );

        assertEquals("John.Smith", firstUsername, "First username should match");
        assertEquals("Mike.Brown", secondUsername, "Second username should match");
    }

    @Test
    void shouldGenerateNonBlankUsername() {
        when(userRepository.existsByUsername("John.Smith")).thenReturn(false);

        String username = usernameGenerator.generate("John", "Smith");

        assertNotNull(
                username,
                "Generated username should not be null"
        );

        assertFalse(
                username.isBlank(),
                "Generated username should not be blank"
        );
    }
}