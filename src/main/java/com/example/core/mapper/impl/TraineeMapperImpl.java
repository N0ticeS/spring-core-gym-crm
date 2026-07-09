package com.example.core.mapper.impl;

import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.mapper.TraineeMapper;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TraineeMapperImpl implements TraineeMapper {
    @Override
    public Trainee toEntity(CreateTraineeRequestDto dto) {
        return Trainee.builder()
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .build();
    }

    @Override
    public void updateEntity(UpdateTraineeRequestDto dto, Trainee trainee) {
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());

        trainee.getUser().setFirstName(dto.getFirstName());
        trainee.getUser().setLastName(dto.getLastName());
    }

    @Override
    public TraineeResponseDto toResponseDto(Trainee trainee) {
        return TraineeResponseDto.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .username(trainee.getUser().getUsername())
                .active(trainee.getUser().isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .trainers(trainee.getTrainers().stream()
                        .map(this::getTrainerFullName).collect(Collectors.toSet()))
                .build();

    }

    private String getTrainerFullName(Trainer trainer) {
        return trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName();
    }
}
