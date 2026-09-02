package com.example.trainer_workload_service.controller;

import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;
import com.example.trainer_workload_service.security.jwt.JwtAuthenticationFilter;
import com.example.trainer_workload_service.security.jwt.JwtService;
import com.example.trainer_workload_service.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainerWorkloadController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        ))
@AutoConfigureMockMvc(addFilters = false)
class TrainerWorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainerWorkloadService trainerWorkloadService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getWorkloadShouldReturn200AndResponseDto() throws Exception {
        var responseDto = TrainerWorkloadResponseDto.builder()
                .trainerUsername("Mike.Johnson")
                .trainerFirstName("Mike")
                .trainerLastName("Johnson")
                .active(true)
                .year(2026)
                .month(8)
                .trainingSummaryDuration(90)
                .build();

        when(trainerWorkloadService.getWorkload(
                "Mike.Johnson",
                2026,
                8
        )).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/workloads/Mike.Johnson")
                        .param("year", "2026")
                        .param("month", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername")
                        .value("Mike.Johnson"))
                .andExpect(jsonPath("$.trainerFirstName")
                        .value("Mike"))
                .andExpect(jsonPath("$.trainerLastName")
                        .value("Johnson"))
                .andExpect(jsonPath("$.active")
                        .value(true))
                .andExpect(jsonPath("$.year")
                        .value(2026))
                .andExpect(jsonPath("$.month")
                        .value(8))
                .andExpect(jsonPath("$.trainingSummaryDuration")
                        .value(90));

        verify(trainerWorkloadService)
                .getWorkload("Mike.Johnson", 2026, 8);
    }

    @Test
    void getWorkloadShouldReturn400WhenYearIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/workloads/Mike.Johnson")
                        .param("month", "8"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainerWorkloadService);
    }

    @Test
    void getWorkloadShouldReturn400WhenMonthIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/workloads/Mike.Johnson")
                        .param("year", "2026"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(trainerWorkloadService);
    }
}
