package com.example.core.controller;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.LoginRequestDto;
import com.example.core.dto.error.ErrorResponseDto;
import com.example.core.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Operation for user authentication and password management"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Authenticate user",
            description = "Validates user credentials"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @GetMapping("/login")
    public ResponseEntity<Void> login(@Valid @ModelAttribute LoginRequestDto request) {

        authService.validateAuthentication(request);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Change user password",
            description = "Changes the password for the specified user after validating the current password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "Invalid current password",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PutMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String username,
            @Valid @RequestBody ChangePasswordRequestDto request) {

        authService.changePassword(username, request);

        return ResponseEntity.ok().build();
    }
}
