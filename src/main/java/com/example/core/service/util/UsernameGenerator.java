package com.example.core.service.util;

import java.util.Set;

public final class UsernameGenerator {
    private static final String USERNAME_SEPARATOR = ".";

    private UsernameGenerator() {
    }

    public static String generate(String firstName, String lastName, Set<String> existingUsername) {
        validateInput(firstName, lastName);

        var baseUsername = firstName + USERNAME_SEPARATOR + lastName;

        if (!existingUsername.contains(baseUsername)) {
            return baseUsername;
        }

        var suffix = 1;
        String usernameWithSuffix;
        do {
            usernameWithSuffix = baseUsername + suffix;
            suffix++;
        } while (existingUsername.contains(usernameWithSuffix));

        return usernameWithSuffix;
    }

    private static void validateInput(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName is required");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName is required");
        }
    }
}
