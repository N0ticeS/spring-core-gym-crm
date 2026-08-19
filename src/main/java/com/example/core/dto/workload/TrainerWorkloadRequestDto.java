package com.example.core.dto.workload;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TrainerWorkloadRequestDto {
    private String trainerUsername;
    private String trainerLastName;
    private String trainerFirstName;
    private Boolean active;
    private LocalDate trainingDate;
    private Integer trainingDuration;
    private ActionType actionType;
}
