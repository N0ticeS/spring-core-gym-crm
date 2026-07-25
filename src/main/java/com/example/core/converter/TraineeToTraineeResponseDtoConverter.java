package com.example.core.converter;

import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainer.TrainerShortResponseDto;
import com.example.core.model.Trainee;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TraineeToTraineeResponseDtoConverter implements Converter<Trainee, TraineeResponseDto> {

    @Override
    public TraineeResponseDto convert(Trainee source) {
        return TraineeResponseDto.builder()
                .firstName(source.getUser().getFirstName())
                .lastName(source.getUser().getLastName())
                .username(source.getUser().getUsername())
                .active(source.getUser().isActive())
                .dateOfBirth(source.getDateOfBirth())
                .address(source.getAddress())
                .trainers(
                        source.getTrainers().stream()
                                .map(trainer -> TrainerShortResponseDto.builder()
                                        .username(trainer.getUser().getUsername())
                                        .firstName(trainer.getUser().getFirstName())
                                        .lastName(trainer.getUser().getLastName())
                                        .specialization(
                                                trainer.getSpecialization()
                                                        .getTrainingTypeName()
                                        )
                                        .build())
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
