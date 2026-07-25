package com.example.core.controller;

import com.example.core.converter.TraineeToTraineeResponseDtoConverter;
import com.example.core.converter.TrainerToTrainerResponseDtoConverter;
import com.example.core.converter.TrainingToTrainingResponseDtoConverter;
import com.example.core.converter.UserToCreatedProfileResponseDtoConverter;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.User;
import com.example.core.service.TraineeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TraineeToTraineeResponseDtoConverter traineeResponseConverter;

    @MockitoBean
    private TrainerToTrainerResponseDtoConverter trainerResponseConverter;

    @MockitoBean
    private TrainingToTrainingResponseDtoConverter trainingResponseConverter;

    @MockitoBean
    private UserToCreatedProfileResponseDtoConverter createdProfileResponseConverter;

    @Test
    void shouldCreateTraineeSuccessfully() throws Exception {
        User user = User.builder()
                .username("John.Smith")
                .password("password123")
                .build();

        CreatedProfileResponseDto response = CreatedProfileResponseDto.builder()
                .username("John.Smith")
                .password("password123")
                .build();

        when(traineeService.create(any()))
                .thenReturn(user);

        when(createdProfileResponseConverter.convert(user))
                .thenReturn(response);

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "John",
                                    "lastName": "Smith",
                                    "dateOfBirth": "2000-01-01",
                                    "address": "New York"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(traineeService).create(any());
        verify(createdProfileResponseConverter).convert(user);
    }

    @Test
    void shouldFindTraineeByUsernameSuccessfully() throws Exception {
        Trainee trainee = Trainee.builder()
                .user(User.builder()
                        .firstName("John")
                        .lastName("Smith")
                        .username("John.Smith")
                        .isActive(true)
                        .build())
                .build();

        TraineeResponseDto response = TraineeResponseDto.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .active(true)
                .build();

        when(traineeService.findByUsername("John.Smith"))
                .thenReturn(trainee);

        when(traineeResponseConverter.convert(trainee))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainees/John.Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.active").value(true));

        verify(traineeService).findByUsername("John.Smith");
        verify(traineeResponseConverter).convert(trainee);
    }

    @Test
    void shouldReturnNotFoundWhenTraineeDoesNotExist() throws Exception {
        when(traineeService.findByUsername("Unknown.User"))
                .thenThrow(new EntityNotFoundException("Trainee profile not found"));

        mockMvc.perform(get("/api/trainees/Unknown.User"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Trainee profile not found"))
                .andExpect(jsonPath("$.path").value("/api/trainees/Unknown.User"));
    }

    @Test
    void shouldFindAllTraineesSuccessfully() throws Exception {
        Trainee trainee = Trainee.builder()
                .user(User.builder()
                        .firstName("John")
                        .lastName("Smith")
                        .username("John.Smith")
                        .isActive(true)
                        .build())
                .build();

        TraineeResponseDto response = TraineeResponseDto.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .active(true)
                .build();

        when(traineeService.findAll())
                .thenReturn(List.of(trainee));

        when(traineeResponseConverter.convert(trainee))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("John.Smith"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"));

        verify(traineeService).findAll();
        verify(traineeResponseConverter).convert(trainee);
    }

    @Test
    void shouldUpdateTraineeSuccessfully() throws Exception {
        Trainee trainee = Trainee.builder()
                .user(User.builder()
                        .firstName("John")
                        .lastName("Updated")
                        .username("John.Smith")
                        .isActive(true)
                        .build())
                .build();

        TraineeResponseDto response = TraineeResponseDto.builder()
                .firstName("John")
                .lastName("Updated")
                .username("John.Smith")
                .active(true)
                .build();

        when(traineeService.update(anyString(), any()))
                .thenReturn(trainee);

        when(traineeResponseConverter.convert(trainee))
                .thenReturn(response);

        mockMvc.perform(put("/api/trainees/John.Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "John",
                                    "lastName": "Updated",
                                    "dateOfBirth": "2000-01-01",
                                    "address": "New York"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Updated"));

        verify(traineeService).update(anyString(), any());
        verify(traineeResponseConverter).convert(trainee);
    }

    @Test
    void shouldChangeTraineeStatusSuccessfully() throws Exception {
        doNothing()
                .when(traineeService)
                .changeStatus("John.Smith", false);

        mockMvc.perform(patch("/api/trainees/John.Smith/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "active": false
                                }
                                """))
                .andExpect(status().isOk());

        verify(traineeService).changeStatus("John.Smith", false);
    }

    @Test
    void shouldDeleteTraineeSuccessfully() throws Exception {
        doNothing()
                .when(traineeService)
                .deleteByUsername("John.Smith");

        mockMvc.perform(delete("/api/trainees/John.Smith"))
                .andExpect(status().isOk());

        verify(traineeService).deleteByUsername("John.Smith");
    }

    @Test
    void shouldGetTraineeTrainingsSuccessfully() throws Exception {
        Training training = Training.builder()
                .trainingName("Morning Training")
                .build();

        TrainingResponseDto response = TrainingResponseDto.builder()
                .trainingName("Morning Training")
                .build();

        when(traineeService.getTrainings(anyString(), any()))
                .thenReturn(List.of(training));

        when(trainingResponseConverter.convert(training))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainees/John.Smith/trainings")
                        .param("fromDate", "2026-01-01")
                        .param("toDate", "2026-12-31")
                        .param("trainerName", "Mike")
                        .param("trainingTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Training"));

        verify(traineeService).getTrainings(anyString(), any());
        verify(trainingResponseConverter).convert(training);
    }

    @Test
    void shouldGetUnassignedTrainersSuccessfully() throws Exception {
        Trainer trainer = Trainer.builder()
                .user(User.builder()
                        .firstName("Mike")
                        .lastName("Brown")
                        .username("Mike.Brown")
                        .isActive(true)
                        .build())
                .build();

        TrainerResponseDto response = TrainerResponseDto.builder()
                .firstName("Mike")
                .lastName("Brown")
                .username("Mike.Brown")
                .active(true)
                .specialization("Fitness")
                .build();

        when(traineeService.getNotAssignedTrainers("John.Smith"))
                .thenReturn(List.of(trainer));

        when(trainerResponseConverter.convert(trainer))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainees/John.Smith/trainers/unassigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Mike.Brown"))
                .andExpect(jsonPath("$[0].firstName").value("Mike"))
                .andExpect(jsonPath("$[0].specialization").value("Fitness"));

        verify(traineeService).getNotAssignedTrainers("John.Smith");
        verify(trainerResponseConverter).convert(trainer);
    }

    @Test
    void shouldUpdateTraineeTrainersSuccessfully() throws Exception {
        Trainee trainee = Trainee.builder()
                .user(User.builder()
                        .firstName("John")
                        .lastName("Smith")
                        .username("John.Smith")
                        .isActive(true)
                        .build())
                .build();

        TraineeResponseDto response = TraineeResponseDto.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .active(true)
                .build();

        when(traineeService.updateTrainers(anyString(), any()))
                .thenReturn(trainee);

        when(traineeResponseConverter.convert(trainee))
                .thenReturn(response);

        mockMvc.perform(put("/api/trainees/John.Smith/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "trainerUsernames": [
                                        "Mike.Brown",
                                        "Anna.Wilson"
                                    ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Smith"));

        verify(traineeService).updateTrainers(anyString(), any());
        verify(traineeResponseConverter).convert(trainee);
    }

    @Test
    void shouldReturnBadRequestWhenCreateTraineeRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/trainees")
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