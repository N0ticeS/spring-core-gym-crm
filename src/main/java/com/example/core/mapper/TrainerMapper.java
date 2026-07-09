package com.example.core.mapper;

import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.model.Trainer;

public interface TrainerMapper {

    Trainer toEntity(CreateTrainerRequestDto dto);

    void updateEntity(UpdateTrainerRequestDto dto, Trainer trainer);

    TrainerResponseDto toResponseDto(Trainer trainer);
}
