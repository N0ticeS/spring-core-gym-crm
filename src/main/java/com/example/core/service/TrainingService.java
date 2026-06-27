package com.example.core.service;

import com.example.core.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingService {

    Training create(Training training);

    Optional<Training> getTrainingById(long id);

    List<Training> findAll();
}
