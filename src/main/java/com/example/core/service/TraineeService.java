package com.example.core.service;

import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.User;
import com.example.core.specification.TrainingSearchCriteria;

import java.util.List;
import java.util.Set;

public interface TraineeService {

    User create(CreateTraineeRequestDto request);

    Trainee findByUsername(String username);

    List<Trainee> findAll();

    Trainee update(String username, UpdateTraineeRequestDto request);

    void changeStatus(String username, boolean active);

    void deleteByUsername(String username);

    List<Training> getTrainings(String username, TrainingSearchCriteria criteria);

    List<Trainer> getNotAssignedTrainers(String username);

    Trainee updateTrainers(String username, Set<String> trainerUsernames);
}
