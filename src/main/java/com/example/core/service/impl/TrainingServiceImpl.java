package com.example.core.service.impl;

import com.example.core.converter.CreateTrainingRequestToTrainingConverter;
import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.repository.TraineeRepository;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.service.TrainingService;
import com.example.core.specification.TrainingSearchCriteria;
import com.example.core.specification.TrainingSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final CreateTrainingRequestToTrainingConverter createTrainingConverter;

    @Override
    @Transactional
    public Training createTraining(CreateTrainingRequestDto request) {
        log.debug("Creating training request, trainee username {}, trainer username {}",
                request.getTraineeUsername(), request.getTrainerUsername());

        var trainee = findTraineeByUsername(request.getTraineeUsername());
        var trainer = findTrainerByUsername(request.getTrainerUsername());

        var training = Objects.requireNonNull(
                createTrainingConverter.convert(request),
                "Failed to convert CreateTrainingRequestDto to Training"
        );

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainer.getSpecialization());

        var savedTraining = trainingRepository.save(training);

        log.info("Training created successfully, id {}, trainee username {}, trainer username {}",
                savedTraining.getId(),
                savedTraining.getTrainee().getUser().getUsername(),
                savedTraining.getTrainer().getUser().getUsername());

        return savedTraining;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findAll(TrainingSearchCriteria criteria) {
        log.debug("Searching trainings with criteria {}", criteria);

        var trainings = trainingRepository
                .findAll(TrainingSpecification.byCriteria(criteria));

        log.info("Trainings found, count {}", trainings.size());

        return trainings;
    }

    private Trainee findTraineeByUsername(String username) {
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee profile not found, username {}", username);
                    return new EntityNotFoundException("Trainee profile not found");
                });
    }

    private Trainer findTrainerByUsername(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer profile not found, username {}", username);
                    return new EntityNotFoundException("Trainer profile not found");
                });
    }
}
