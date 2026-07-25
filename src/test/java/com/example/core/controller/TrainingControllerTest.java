package com.example.core.controller;

import com.example.core.converter.TrainingToTrainingResponseDtoConverter;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.model.Training;
import com.example.core.service.TrainingService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingService trainingService;

    @MockitoBean
    private TrainingToTrainingResponseDtoConverter trainingResponseConverter;

    @Test
    void shouldCreateTrainingSuccessfully() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "traineeUsername": "John.Smith",
                                    "trainerUsername": "Mike.Brown",
                                    "trainingName": "Morning Training",
                                    "trainingDate": "2030-08-19",
                                    "trainingDuration": 60
                                }
                                """))
                .andExpect(status().isOk());

        verify(trainingService).createTraining(any());
    }

    @Test
    void shouldReturnNotFoundWhenRelatedEntityDoesNotExist() throws Exception {
        doThrow(new EntityNotFoundException("Trainer profile not found"))
                .when(trainingService)
                .createTraining(any());

        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "traineeUsername": "John.Smith",
                                    "trainerUsername": "Unknown.Trainer",
                                    "trainingName": "Morning Training",
                                    "trainingDate": "2030-07-19",
                                    "trainingDuration": 60
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Trainer profile not found"))
                .andExpect(jsonPath("$.path").value("/api/trainings"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateTrainingRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "traineeUsername": "",
                                    "trainerUsername": "",
                                    "trainingName": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllTrainingsSuccessfully() throws Exception {
        Training training = Training.builder()
                .trainingName("Morning Training")
                .build();

        TrainingResponseDto response = TrainingResponseDto.builder()
                .trainingName("Morning Training")
                .build();

        when(trainingService.findAll(any()))
                .thenReturn(List.of(training));

        when(trainingResponseConverter.convert(training))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Training"));

        verify(trainingService).findAll(any());
        verify(trainingResponseConverter).convert(training);
    }

    @Test
    void shouldGetAllTrainingsWithFiltersSuccessfully() throws Exception {
        Training training = Training.builder()
                .trainingName("Morning Training")
                .build();

        TrainingResponseDto response = TrainingResponseDto.builder()
                .trainingName("Morning Training")
                .build();

        when(trainingService.findAll(any()))
                .thenReturn(List.of(training));

        when(trainingResponseConverter.convert(training))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainings")
                        .param("fromDate", "2026-01-01")
                        .param("toDate", "2026-12-31")
                        .param("trainerName", "Mike")
                        .param("traineeName", "John")
                        .param("trainingTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Training"));

        verify(trainingService).findAll(any());
        verify(trainingResponseConverter).convert(training);
    }
}