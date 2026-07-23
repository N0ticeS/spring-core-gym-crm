package com.example.core.controller;

import com.example.core.converter.TrainingTypeResponseConverter;
import com.example.core.dto.trainingtype.TrainingTypeResponseDto;
import com.example.core.model.TrainingType;
import com.example.core.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingTypeController.class)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @MockitoBean
    private TrainingTypeResponseConverter trainingTypeResponseConverter;

    @Test
    void shouldGetTrainingTypesSuccessfully() throws Exception {
        TrainingType fitness = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();

        TrainingType yoga = TrainingType.builder()
                .id(2L)
                .trainingTypeName("Yoga")
                .build();

        TrainingTypeResponseDto fitnessResponse = TrainingTypeResponseDto.builder()
                .id(1L)
                .trainingType("Fitness")
                .build();

        TrainingTypeResponseDto yogaResponse = TrainingTypeResponseDto.builder()
                .id(2L)
                .trainingType("Yoga")
                .build();

        when(trainingTypeService.findAll())
                .thenReturn(List.of(fitness, yoga));

        when(trainingTypeResponseConverter.convert(fitness))
                .thenReturn(fitnessResponse);

        when(trainingTypeResponseConverter.convert(yoga))
                .thenReturn(yogaResponse);

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trainingType").value("Fitness"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].trainingType").value("Yoga"));

        verify(trainingTypeService).findAll();
        verify(trainingTypeResponseConverter).convert(fitness);
        verify(trainingTypeResponseConverter).convert(yoga);
    }

    @Test
    void shouldReturnEmptyListWhenTrainingTypesDoNotExist() throws Exception {
        when(trainingTypeService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(trainingTypeService).findAll();
    }
}