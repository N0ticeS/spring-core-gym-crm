package com.example.core.specification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSearchCriteria {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeName;
    private String trainerName;
    private String traineeUsername;
    private String trainerUsername;
    private Long trainingTypeId;
}
