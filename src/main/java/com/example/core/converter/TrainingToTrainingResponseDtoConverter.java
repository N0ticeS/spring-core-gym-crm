package com.example.core.converter;

import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.model.Training;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingToTrainingResponseDtoConverter implements Converter<Training, TrainingResponseDto> {

    @Override
    public TrainingResponseDto convert(Training source) {
        return TrainingResponseDto.builder()
                .trainingName(source.getTrainingName())
                .trainingDate(source.getTrainingDate())
                .trainingDuration(source.getTrainingDuration())
                .trainingType(source.getTrainingType().getTrainingTypeName())
                .traineeUsername(source.getTrainee().getUser().getUsername())
                .trainerUsername(source.getTrainer().getUser().getUsername())
                .trainerName(getTrainerFullName(source))
                .build();
    }

    private String getTrainerFullName(Training training) {
        return training.getTrainer().getUser().getFirstName()
                + " "
                + training.getTrainer().getUser().getLastName();
    }
}
