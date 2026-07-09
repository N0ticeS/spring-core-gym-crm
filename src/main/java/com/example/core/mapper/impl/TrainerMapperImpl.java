package com.example.core.mapper.impl;

import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.mapper.TrainerMapper;
import com.example.core.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapperImpl implements TrainerMapper {

    @Override
    public Trainer toEntity(CreateTrainerRequestDto dto) {
        return Trainer.builder().build();
    }

    @Override
    public void updateEntity(UpdateTrainerRequestDto dto, Trainer trainer) {
        trainer.getUser().setFirstName(dto.getFirstName());
        trainer.getUser().setLastName(dto.getLastName());
    }

    @Override
    public TrainerResponseDto toResponseDto(Trainer trainer) {
        return TrainerResponseDto.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .username(trainer.getUser().getUsername())
                .active(trainer.getUser().isActive())
                .specialization(trainer.getSpecialization().getTrainingTypeName())
                .build();
    }
}