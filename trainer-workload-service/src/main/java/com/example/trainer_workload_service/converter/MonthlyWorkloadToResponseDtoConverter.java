package com.example.trainer_workload_service.converter;

import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;
import com.example.trainer_workload_service.model.MonthlyWorkload;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MonthlyWorkloadToResponseDtoConverter
        implements Converter<MonthlyWorkload, TrainerWorkloadResponseDto> {

    @Override
    public TrainerWorkloadResponseDto convert(MonthlyWorkload source) {
        return TrainerWorkloadResponseDto.builder()
                .trainerUsername(source.getTrainer().getUsername())
                .trainerFirstName(source.getTrainer().getFirstName())
                .trainerLastName(source.getTrainer().getLastName())
                .active(source.getTrainer().getActive())
                .year(source.getYear())
                .month(source.getMonth())
                .trainingSummaryDuration(
                        source.getTrainingSummaryDuration()
                )
                .build();
    }
}
