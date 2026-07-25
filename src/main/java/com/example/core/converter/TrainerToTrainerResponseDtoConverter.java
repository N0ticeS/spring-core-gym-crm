package com.example.core.converter;

import com.example.core.dto.trainee.TraineeShortResponseDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.model.Trainer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TrainerToTrainerResponseDtoConverter implements Converter<Trainer, TrainerResponseDto> {
    @Override
    public TrainerResponseDto convert(Trainer source) {
        return TrainerResponseDto.builder()
                .firstName(source.getUser().getFirstName())
                .lastName(source.getUser().getLastName())
                .username(source.getUser().getUsername())
                .active(source.getUser().isActive())
                .specialization(source.getSpecialization().getTrainingTypeName())
                .trainees(
                        source.getTrainees().stream()
                                .map(trainees -> TraineeShortResponseDto.builder()
                                        .username(trainees.getUser().getUsername())
                                        .firstName(trainees.getUser().getFirstName())
                                        .lastName(trainees.getUser().getLastName())
                                        .build()
                                )
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
