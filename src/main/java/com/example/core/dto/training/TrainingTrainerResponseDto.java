package com.example.core.dto.training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTrainerResponseDto {
    private String trainingName;

    private LocalDate trainingDate;

    private Integer trainingDuration;

    private String trainingType;

    private String traineeUsername;

    private String trainerUsername;

    private String traineeName;
}
