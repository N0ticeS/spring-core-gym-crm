package com.example.core.controller;

import com.example.core.converter.JwtToLoginResponseDtoConverter;
import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.LoginRequestDto;
import com.example.core.dto.auth.LoginResponseDto;
import com.example.core.dto.error.ErrorResponseDto;
import com.example.core.security.jwt.JwtService;
import com.example.core.security.service.CustomUserDetails;
import com.example.core.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    private final JwtService jwtService;
    private final JwtToLoginResponseDtoConverter jwtToLoginResponseDtoConverter;

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
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {

        var authentication = authService.authenticate(request);

        var userDetails = (CustomUserDetails) authentication.getPrincipal();

        var token = jwtService.generateToken(userDetails);

        var response = jwtToLoginResponseDtoConverter.convert(token);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Change user password",
            description = "Changes the password for the specified user after validating the current password",
            security = @SecurityRequirement(name = "bearerAuth")
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
