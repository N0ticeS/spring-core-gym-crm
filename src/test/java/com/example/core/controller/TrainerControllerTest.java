package com.example.core.controller;

import com.example.core.converter.TrainerToTrainerResponseDtoConverter;
import com.example.core.converter.TrainingToTrainerResponseDtoConverter;
import com.example.core.converter.UserToCreatedProfileResponseDtoConverter;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.training.TrainingTrainerResponseDto;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.TrainingType;
import com.example.core.model.User;
import com.example.core.service.TrainerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private TrainerToTrainerResponseDtoConverter trainerResponseConverter;

    @MockitoBean
    private TrainingToTrainerResponseDtoConverter trainingToTrainerConverter;

    @MockitoBean
    private UserToCreatedProfileResponseDtoConverter createdProfileResponseConverter;

    @Test
    void shouldCreateTrainerSuccessfully() throws Exception {
        User user = User.builder()
                .username("Mike.Brown")
                .password("password123")
                .build();

        CreatedProfileResponseDto response = CreatedProfileResponseDto.builder()
                .username("Mike.Brown")
                .password("password123")
                .build();

        when(trainerService.create(any()))
                .thenReturn(user);

        when(createdProfileResponseConverter.convert(user))
                .thenReturn(response);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Mike",
                                    "lastName": "Brown",
                                    "specialization": "Yoga"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Mike.Brown"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(trainerService).create(any());
        verify(createdProfileResponseConverter).convert(user);
    }

    @Test
    void shouldFindTrainerByUsernameSuccessfully() throws Exception {
        Trainer trainer = Trainer.builder()
                .user(User.builder()
                        .firstName("Mike")
                        .lastName("Brown")
                        .username("Mike.Brown")
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder()
                        .trainingTypeName("Fitness")
                        .build())
                .build();

        TrainerResponseDto response = TrainerResponseDto.builder()
                .firstName("Mike")
                .lastName("Brown")
                .username("Mike.Brown")
                .active(true)
                .specialization("Fitness")
                .build();

        when(trainerService.findByUsername("Mike.Brown"))
                .thenReturn(trainer);

        when(trainerResponseConverter.convert(trainer))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainers/Mike.Brown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Mike"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.username").value("Mike.Brown"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.specialization").value("Fitness"));

        verify(trainerService).findByUsername("Mike.Brown");
        verify(trainerResponseConverter).convert(trainer);
    }

    @Test
    void shouldReturnNotFoundWhenTrainerDoesNotExist() throws Exception {
        when(trainerService.findByUsername("Unknown.User"))
                .thenThrow(new EntityNotFoundException("Trainer profile not found"));

        mockMvc.perform(get("/api/trainers/Unknown.User"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Trainer profile not found"))
                .andExpect(jsonPath("$.path").value("/api/trainers/Unknown.User"));
    }

    @Test
    void shouldFindAllTrainersSuccessfully() throws Exception {
        Trainer trainer = Trainer.builder()
                .user(User.builder()
                        .firstName("Mike")
                        .lastName("Brown")
                        .username("Mike.Brown")
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder()
                        .trainingTypeName("Fitness")
                        .build())
                .build();

        TrainerResponseDto response = TrainerResponseDto.builder()
                .firstName("Mike")
                .lastName("Brown")
                .username("Mike.Brown")
                .active(true)
                .specialization("Fitness")
                .build();

        when(trainerService.findAll())
                .thenReturn(List.of(trainer));

        when(trainerResponseConverter.convert(trainer))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Mike.Brown"))
                .andExpect(jsonPath("$[0].firstName").value("Mike"))
                .andExpect(jsonPath("$[0].lastName").value("Brown"))
                .andExpect(jsonPath("$[0].specialization").value("Fitness"));

        verify(trainerService).findAll();
        verify(trainerResponseConverter).convert(trainer);
    }

    @Test
    void shouldUpdateTrainerSuccessfully() throws Exception {
        Trainer trainer = Trainer.builder()
                .user(User.builder()
                        .firstName("Mike")
                        .lastName("Updated")
                        .username("Mike.Brown")
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder()
                        .trainingTypeName("Fitness")
                        .build())
                .build();

        TrainerResponseDto response = TrainerResponseDto.builder()
                .firstName("Mike")
                .lastName("Updated")
                .username("Mike.Brown")
                .active(true)
                .specialization("Fitness")
                .build();

        when(trainerService.update(anyString(), any()))
                .thenReturn(trainer);

        when(trainerResponseConverter.convert(trainer))
                .thenReturn(response);

        mockMvc.perform(put("/api/trainers/Mike.Brown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Mike",
                                    "lastName": "Updated"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Mike.Brown"))
                .andExpect(jsonPath("$.firstName").value("Mike"))
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.specialization").value("Fitness"));

        verify(trainerService).update(anyString(), any());
        verify(trainerResponseConverter).convert(trainer);
    }

    @Test
    void shouldChangeTrainerStatusSuccessfully() throws Exception {
        doNothing()
                .when(trainerService)
                .changeStatus("Mike.Brown", false);

        mockMvc.perform(patch("/api/trainers/Mike.Brown/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "active": false
                                }
                                """))
                .andExpect(status().isOk());

        verify(trainerService).changeStatus("Mike.Brown", false);
    }

    @Test
    void shouldGetTrainerTrainingsSuccessfully() throws Exception {
        Training training = Training.builder()
                .trainingName("Morning Training")
                .build();

        TrainingTrainerResponseDto response = TrainingTrainerResponseDto.builder()
                .trainingName("Morning Training")
                .build();

        when(trainerService.getTrainings(anyString(), any()))
                .thenReturn(List.of(training));

        when(trainingToTrainerConverter.convert(training))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainers/Mike.Brown/trainings")
                        .param("fromDate", "2026-01-01")
                        .param("toDate", "2026-12-31")
                        .param("traineeName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Training"));

        verify(trainerService).getTrainings(anyString(), any());
        verify(trainingToTrainerConverter).convert(training);
    }

    @Test
    void shouldReturnBadRequestWhenCreateTrainerRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "",
                                    "lastName": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}