package com.example.core.service;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.specification.TrainingSearchCriteria;

import java.util.List;
import java.util.Set;

public interface TraineeService {

    CreatedProfileResponseDto create(CreateTraineeRequestDto request);

    TraineeResponseDto findByUsername(String username);

    List<TraineeResponseDto> findAll();

    TraineeResponseDto update(String username, UpdateTraineeRequestDto request);

    void changePassword(String username, ChangePasswordRequestDto request);

    void changeStatus(String username, boolean active);

    void deleteByUsername(String username);

    List<TrainingResponseDto> getTrainings(String username, TrainingSearchCriteria criteria);

    List<TrainerResponseDto> getNotAssignedTrainers(String username);

    TraineeResponseDto updateTrainers(String username, Set<String> trainerUsernames);
}
