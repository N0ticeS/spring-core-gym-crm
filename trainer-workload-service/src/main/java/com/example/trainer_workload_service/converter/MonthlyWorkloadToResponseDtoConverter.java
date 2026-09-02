package com.example.trainer_workload_service.converter;

import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;
import com.example.trainer_workload_service.model.MonthlySummary;
import com.example.trainer_workload_service.model.TrainerWorkload;
import com.example.trainer_workload_service.model.YearSummary;
import org.springframework.stereotype.Component;

@Component
public class MonthlyWorkloadToResponseDtoConverter {

    public TrainerWorkloadResponseDto convert(
            TrainerWorkload trainer,
            YearSummary yearSummary,
            MonthlySummary monthlySummary
    ) {
        return TrainerWorkloadResponseDto.builder()
                .trainerUsername(trainer.getUsername())
                .trainerFirstName(trainer.getFirstName())
                .trainerLastName(trainer.getLastName())
                .active(trainer.getActive())
                .year(yearSummary.getYear())
                .month(monthlySummary.getMonth())
                .trainingSummaryDuration(monthlySummary.getTrainingSummaryDuration())
                .build();
    }
}
