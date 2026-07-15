package com.example.core.mapper.impl;

import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.mapper.TrainingMapper;
import com.example.core.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapperImpl implements TrainingMapper {

    @Override
    public Training toEntity(CreateTrainingRequestDto dto) {
        return Training.builder()
                .trainingName(dto.getTrainingName())
                .trainingDate(dto.getTrainingDate())
                .trainingDuration(dto.getTrainingDuration())
                .build();
    }

    @Override
    public TrainingResponseDto toResponseDto(Training training) {
        return TrainingResponseDto.builder()
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .trainingType(training.getTrainingType().getTrainingTypeName())
                .traineeUsername(training.getTrainee().getUser().getUsername())
                .trainerUsername(training.getTrainer().getUser().getUsername())
                .trainerName(getTrainerFullName(training))
                .build();
    }

    private String getTrainerFullName(Training training) {
        return training.getTrainer().getUser().getFirstName()
                + " "
                + training.getTrainer().getUser().getLastName();
    }
}
