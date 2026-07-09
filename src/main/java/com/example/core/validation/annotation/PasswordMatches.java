package com.example.core.validation.annotation;

import com.example.core.validation.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {

    String message() default "Password and confirmation password must match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
