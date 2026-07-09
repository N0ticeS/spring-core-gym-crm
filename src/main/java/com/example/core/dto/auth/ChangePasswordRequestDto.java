package com.example.core.dto.auth;

import com.example.core.validation.PasswordConfirmation;
import com.example.core.validation.annotation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@PasswordMatches
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequestDto implements PasswordConfirmation {

    @NotBlank(message = "Current password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 64, message = "Password must contain between 8 and 64 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}
