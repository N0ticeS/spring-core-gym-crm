package com.example.core.dto.training;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainingRequestDto {

    @NotBlank(message = "Training name is required")
    @Size(max = 100, message = "Training name cannot exceed 100 characters")
    private String trainingName;

    @NotNull(message = "Training date is required")
    @FutureOrPresent(message = "Training date cannot be in the past")
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @Min(value = 1, message = "Training duration must be greater than 0")
    private Integer trainingDuration;

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;
}
