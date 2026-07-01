package com.example.core.utils;

import com.example.core.service.util.UsernameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsernameGeneratorTest {

    @Test
    void shouldGenerateBaseUsernameWhenUsernameDoesNotExist() {
        var existingUsernames = Set.of("Mike.Brown");

        var result = UsernameGenerator.generate("John", "Smith", existingUsernames);

        assertEquals(
                "John.Smith",
                result,
                "Generated username should match base username when it is unique"
        );
    }

    @Test
    void shouldGenerateUsernameWithSuffixOneWhenBaseUsernameExists() {
        var existingUsernames = Set.of("John.Smith");

        var result = UsernameGenerator.generate("John", "Smith", existingUsernames);

        assertEquals(
                "John.Smith1",
                result,
                "Generated username should contain suffix 1 when base username already exists"
        );
    }

    @Test
    void shouldGenerateUsernameWithNextAvailableSuffixWhenSeveralUsernamesExist() {
        var existingUsernames = Set.of(
                "John.Smith",
                "John.Smith1",
                "John.Smith2"
        );

        var result = UsernameGenerator.generate("John", "Smith", existingUsernames);

        assertEquals(
                "John.Smith3",
                result,
                "Generated username should use the next available numeric suffix"
        );
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsNull() {
        var existingUsernames = Set.of("John.Smith");

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> UsernameGenerator.generate(null, "Smith", existingUsernames),
                "Generating username with null first name should throw IllegalArgumentException"
        );

        assertEquals(
                "firstName is required",
                exception.getMessage(),
                "Exception message should describe missing first name"
        );
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsBlank() {
        var existingUsernames = Set.of("John.Smith");

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> UsernameGenerator.generate(" ", "Smith", existingUsernames),
                "Generating username with blank first name should throw IllegalArgumentException"
        );

        assertEquals(
                "firstName is required",
                exception.getMessage(),
                "Exception message should describe blank first name"
        );
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNull() {
        var existingUsernames = Set.of("John.Smith");

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> UsernameGenerator.generate("John", null, existingUsernames),
                "Generating username with null last name should throw IllegalArgumentException"
        );

        assertEquals(
                "lastName is required",
                exception.getMessage(),
                "Exception message should describe missing last name"
        );
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsBlank() {
        var existingUsernames = Set.of("John.Smith");

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> UsernameGenerator.generate("John", " ", existingUsernames),
                "Generating username with blank last name should throw IllegalArgumentException"
        );

        assertEquals(
                "lastName is required",
                exception.getMessage(),
                "Exception message should describe blank last name"
        );
    }
}
