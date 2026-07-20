package com.example.core.service;

import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.User;
import com.example.core.specification.TrainingSearchCriteria;

import java.util.List;

public interface TrainerService {

    User create(CreateTrainerRequestDto request);

    Trainer findByUsername(String username);

    List<Trainer> findAll();

    Trainer update(String username, UpdateTrainerRequestDto request);

    void changeStatus(String username, boolean active);

    void deleteByUsername(String username);

    List<Training> getTrainings(String username, TrainingSearchCriteria criteria);
}
