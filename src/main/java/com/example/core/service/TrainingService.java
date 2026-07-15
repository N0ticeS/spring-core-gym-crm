package com.example.core.service;

import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.specification.TrainingSearchCriteria;

import java.util.List;

public interface TrainingService {

    TrainingResponseDto createTraining(CreateTrainingRequestDto request);

    List<TrainingResponseDto> findAll(TrainingSearchCriteria criteria);

    void deleteById(Long id);
}
