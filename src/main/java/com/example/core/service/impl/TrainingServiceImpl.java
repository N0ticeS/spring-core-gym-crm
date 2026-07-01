package com.example.core.service.impl;

import com.example.core.dao.TrainingDao;
import com.example.core.model.Training;
import com.example.core.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDao trainingDao;

    @Override
    public Training create(Training training) {
        validateTrainingForCreate(training);

        log.debug("Creating training, trainee id {}, trainer id {}, training name {}",
                training.getTraineeId(), training.getTrainerId(), training.getTrainingName());

        var savedTraining = trainingDao.save(training);

        log.info("Training created successfully, id {}, trainee id {}, trainer id {}",
                training.getId(), training.getTraineeId(), training.getTrainerId());

        return savedTraining;
    }

    @Override
    public Optional<Training> getTrainingById(long id) {
        log.debug("Getting training by id {}", id);

        var training = trainingDao.findById(id);

        if (training.isPresent()) {
            log.info("Training found successfully, id {}", id);
        } else {
            log.warn("Training not found, id {}", id);
        }

        return training;
    }

    @Override
    public List<Training> findAll() {
        log.debug("Getting all trainings");

        var trainings = trainingDao.findAll();

        log.info("Found {} trainings", trainings.size());
        return trainings;
    }

    private void validateTrainingForCreate(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be null");
        }

        if (training.getTraineeId() == null) {
            throw new IllegalArgumentException("Training trainee id cannot be null");
        }

        if (training.getTrainerId() == null) {
            throw new IllegalArgumentException("Training trainer id cannot be null");
        }

        if (training.getTrainingName() == null || training.getTrainingName().isBlank()) {
            throw new IllegalArgumentException("Training name cannot be empty");
        }

        if (training.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type cannot be null");
        }

        if (training.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date cannot be null");
        }

        if (training.getTrainingDuration() == null) {
            throw new IllegalArgumentException("Training duration cannot be null");
        }

        if (training.getTrainingDuration() <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }
}
