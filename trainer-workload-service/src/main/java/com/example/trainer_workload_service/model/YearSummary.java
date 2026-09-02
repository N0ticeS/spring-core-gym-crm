package com.example.trainer_workload_service.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YearSummary {

    private Integer year;

    @Builder.Default
    private List<MonthlySummary> months = new ArrayList<>();
}
