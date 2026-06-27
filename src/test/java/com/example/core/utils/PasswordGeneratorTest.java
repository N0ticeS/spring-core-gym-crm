package com.example.core.utils;

import com.example.core.service.util.PasswordGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordGeneratorTest {

    @Test
    void shouldGeneratePasswordWithCorrectLength() {
        var password = PasswordGenerator.generatePassword();

        assertEquals(
                10,
                password.length(),
                "Generated password should have length of 10"
        );
    }

    @Test
    void shouldGeneratePasswordContainingOnlyAllowedCharacters() {
        var password = PasswordGenerator.generatePassword();

        assertTrue(
                password.matches("^[A-Za-z0-9]{10}$"),
                "Generated password should contain only letters and digits"
        );
    }

    @Test
    void shouldGenerateDifferentPasswords() {
        var firstPassword = PasswordGenerator.generatePassword();
        var secondPassword = PasswordGenerator.generatePassword();

        assertNotEquals(
                firstPassword,
                secondPassword,
                "Generated passwords should be different"
        );
    }

    @Test
    void shouldGenerateNonBlankPassword() {
        var password = PasswordGenerator.generatePassword();

        assertNotNull(
                password,
                "Generated password should not be null"
        );

        assertFalse(
                password.isBlank(),
                "Generated password should not be blank"
        );
    }
}
