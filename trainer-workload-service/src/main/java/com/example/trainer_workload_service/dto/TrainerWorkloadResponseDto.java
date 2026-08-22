package com.example.trainer_workload_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerWorkloadResponseDto {

    private String trainerUsername;

    private String trainerFirstName;

    private String trainerLastName;

    private Boolean active;

    private Integer year;

    private Integer month;

    private Integer trainingSummaryDuration;
}
