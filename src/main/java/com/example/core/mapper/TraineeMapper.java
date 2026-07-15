package com.example.core.mapper;

import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.model.Trainee;

public interface TraineeMapper {
    Trainee toEntity(CreateTraineeRequestDto dto);

    void updateEntity(UpdateTraineeRequestDto dto, Trainee trainee);

    TraineeResponseDto toResponseDto(Trainee trainee);
}
