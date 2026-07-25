package com.example.core.controller;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.exception.auth.AuthenticationException;
import com.example.core.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldAuthenticateUserSuccessfully() throws Exception {
        doNothing()
                .when(authService)
                .validateAuthentication(any());

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "John.Smith")
                        .param("password", "password123"))
                .andExpect(status().isOk());

        verify(authService).validateAuthentication(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthenticationFails() throws Exception {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authService)
                .validateAuthentication(any());

        mockMvc.perform(get("/api/auth/login")
                        .param("username", "John.Smith")
                        .param("password", "wrongPassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
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

        doThrow(new AuthenticationException("Invalid username or password"))
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
