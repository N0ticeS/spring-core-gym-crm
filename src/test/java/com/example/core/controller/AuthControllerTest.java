package com.example.core.controller;

import com.example.core.converter.JwtToLoginResponseDtoConverter;
import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.LoginRequestDto;
import com.example.core.dto.auth.LoginResponseDto;
import com.example.core.exception.auth.InvalidCredentialsException;
import com.example.core.security.jwt.JwtAuthenticationFilter;
import com.example.core.security.jwt.JwtService;
import com.example.core.security.service.CustomUserDetails;
import com.example.core.security.service.CustomUserDetailsService;
import com.example.core.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtToLoginResponseDtoConverter jwtToLoginResponseDtoConverter;

    @Test
    void shouldAuthenticateUserSuccessfully() throws Exception {
        var request = LoginRequestDto.builder()
                .username("John.Smith")
                .password("password123")
                .build();

        var authentication = mock(Authentication.class);
        var userDetails = mock(CustomUserDetails.class);

        var token = "test-jwt-token";

        var response = LoginResponseDto.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(3600L)
                .build();

        when(authService.authenticate(any(LoginRequestDto.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn(token);

        when(jwtToLoginResponseDtoConverter.convert(token))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));

        verify(authService).authenticate(any(LoginRequestDto.class));
        verify(jwtService).generateToken(userDetails);
        verify(jwtToLoginResponseDtoConverter).convert(token);
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthenticationFails() throws Exception {
        var request = LoginRequestDto.builder()
                .username("John.Smith")
                .password("wrongPassword")
                .build();

        when(authService.authenticate(any(LoginRequestDto.class)))
                .thenThrow(
                        new InvalidCredentialsException(
                                "Invalid username or password"
                        )
                );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid username or password"))
                .andExpect(jsonPath("$.path")
                        .value("/api/auth/login"));

        verify(authService).authenticate(any(LoginRequestDto.class));

        verifyNoInteractions(
                jwtService,
                jwtToLoginResponseDtoConverter
        );
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        ChangePasswordRequestDto request = ChangePasswordRequestDto.builder()
                .oldPassword("password123")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        doNothing()
                .when(authService)
                .changePassword(any(), any());

        mockMvc.perform(put("/api/auth/John.Smith/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authService).changePassword(any(), any());
    }

    @Test
    void shouldReturnUnauthorizedWhenCurrentPasswordIsInvalid() throws Exception {
        ChangePasswordRequestDto request = ChangePasswordRequestDto.builder()
                .oldPassword("wrongPassword")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        doThrow(new InvalidCredentialsException("Invalid username or password"))
                .when(authService)
                .changePassword(any(), any());

        mockMvc.perform(put("/api/auth/John.Smith/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.path").value("/api/auth/John.Smith/password"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        ChangePasswordRequestDto request = ChangePasswordRequestDto.builder()
                .oldPassword("password123")
                .password("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        doThrow(new EntityNotFoundException("User not found"))
                .when(authService)
                .changePassword(any(), any());

        mockMvc.perform(put("/api/auth/Unknown.User/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.path").value("/api/auth/Unknown.User/password"));
    }

    @Test
    void shouldReturnBadRequestWhenChangePasswordRequestIsInvalid() throws Exception {
        ChangePasswordRequestDto request = ChangePasswordRequestDto.builder()
                .oldPassword("")
                .password("")
                .confirmPassword("")
                .build();

        mockMvc.perform(put("/api/auth/John.Smith/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
