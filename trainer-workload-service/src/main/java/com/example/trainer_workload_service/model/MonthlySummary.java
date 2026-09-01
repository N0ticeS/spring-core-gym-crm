package com.example.trainer_workload_service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySummary {

    private Integer month;

    private Integer trainingSummaryDuration;
}
