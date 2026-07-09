package com.example.core.validation;

import com.example.core.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordConfirmation> {

    @Override
    public boolean isValid(PasswordConfirmation dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();

        if (password == null || confirmPassword == null) {
            return true;
        }

        boolean matches = Objects.equals(password, confirmPassword);

        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return matches;
    }
}
