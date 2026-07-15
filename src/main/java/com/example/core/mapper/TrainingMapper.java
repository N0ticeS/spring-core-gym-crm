package com.example.core.mapper;

import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.model.Training;

public interface TrainingMapper {

    Training toEntity(CreateTrainingRequestDto dto);

    TrainingResponseDto toResponseDto(Training training);
}
