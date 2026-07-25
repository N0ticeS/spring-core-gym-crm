package com.example.core.service;

import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.model.Training;
import com.example.core.specification.TrainingSearchCriteria;

import java.util.List;

public interface TrainingService {

    Training createTraining(CreateTrainingRequestDto request);

    List<Training> findAll(TrainingSearchCriteria criteria);
}
