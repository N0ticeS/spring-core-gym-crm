package com.example.core.service.impl;

import com.example.core.converter.CreateTrainingRequestToTrainingConverter;
import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.dto.workload.ActionType;
import com.example.core.dto.workload.TrainerWorkloadRequestDto;
import com.example.core.messaging.producer.TrainerWorkloadProducer;
import com.example.core.metrics.TrainingCreationMetrics;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final TrainingCreationMetrics trainingCreationMetrics;
    private final TrainerWorkloadProducer trainerWorkloadProducer;

    @Override
    @Transactional
    @PreAuthorize("#request.trainerUsername == authentication.name or hasRole('ADMIN')")
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

        var workloadRequest = buildTrainerWorkloadRequest(savedTraining, ActionType.ADD);
        trainerWorkloadProducer.send(workloadRequest);

        trainingCreationMetrics.increment();

        log.info("Training created successfully, id {}, trainee username {}, trainer username {}",
                savedTraining.getId(),
                savedTraining.getTrainee().getUser().getUsername(),
                savedTraining.getTrainer().getUser().getUsername());

        return savedTraining;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<Training> findAll(TrainingSearchCriteria criteria) {
        log.debug("Searching trainings with criteria {}", criteria);

        var trainings = trainingRepository
                .findAll(TrainingSpecification.byCriteria(criteria));

        log.info("Trainings found, count {}", trainings.size());

        return trainings;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTraining(Long id) {
        log.debug("Deleting training with id {}", id);

        var training = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training with id " + id + " not found"));

        if (training.getTrainingDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Past training cannot be deleted, id: " + id);
        }

        var workloadRequest = buildTrainerWorkloadRequest(training, ActionType.DELETE);
        trainerWorkloadProducer.send(workloadRequest);

        trainingRepository.delete(training);

        log.info("Training deleted successfully, id {}, trainer username {}",
                training.getId(), training.getTrainer().getUser().getUsername());
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

    private TrainerWorkloadRequestDto buildTrainerWorkloadRequest(Training training,
                                                                  ActionType actionType) {
        var trainer = training.getTrainer();
        var user = trainer.getUser();

        return TrainerWorkloadRequestDto.builder()
                .trainerUsername(user.getUsername())
                .trainerLastName(user.getLastName())
                .trainerFirstName(user.getFirstName())
                .active(user.isActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(actionType)
                .build();
    }
}
