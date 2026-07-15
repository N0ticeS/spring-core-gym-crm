package com.example.core.service;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.specification.TrainingSearchCriteria;

import java.util.List;

public interface TrainerService {

    CreatedProfileResponseDto create(CreateTrainerRequestDto request);

    TrainerResponseDto findByUsername(String username);

    List<TrainerResponseDto> findAll();

    TrainerResponseDto update(String username, UpdateTrainerRequestDto request);

    void changePassword(String username, ChangePasswordRequestDto request);

    void changeStatus(String username, boolean active);

    void deleteByUsername(String username);

    List<TrainingResponseDto> getTrainings(String username, TrainingSearchCriteria criteria);
}
