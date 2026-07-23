package com.example.core.converter;


import com.example.core.dto.training.TrainingTrainerResponseDto;
import com.example.core.model.Training;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingToTrainerResponseDtoConverter implements Converter<Training, TrainingTrainerResponseDto> {
    @Override
    public TrainingTrainerResponseDto convert(Training source) {
        return TrainingTrainerResponseDto.builder()
                .trainingName(source.getTrainingName())
                .trainingDate(source.getTrainingDate())
                .trainingDuration(source.getTrainingDuration())
                .trainingType(source.getTrainingType().getTrainingTypeName())
                .traineeUsername(source.getTrainee().getUser().getUsername())
                .trainerUsername(source.getTrainer().getUser().getUsername())
                .traineeName(getTraineeFullName(source))
                .build();
    }

    private String getTraineeFullName(Training training) {
        return training.getTrainee().getUser().getFirstName()
                + " "
                + training.getTrainee().getUser().getLastName();
    }
}
