package com.example.trainer_workload_service.dto;

import com.example.trainer_workload_service.model.ActionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerWorkloadRequestDto {

    @NotBlank
    private String trainerUsername;

    @NotBlank
    private String trainerFirstName;

    @NotBlank
    private String trainerLastName;

    @NotNull
    private Boolean active;

    @NotNull
    private LocalDate trainingDate;

    @NotNull
    @Min(1)
    private Integer trainingDuration;

    @NotNull
    private ActionType actionType;
}
