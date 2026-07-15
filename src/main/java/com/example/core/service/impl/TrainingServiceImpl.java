package com.example.core.service.impl;

import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.mapper.TrainingMapper;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingMapper trainingMapper;

    @Override
    @Transactional
    public TrainingResponseDto createTraining(CreateTrainingRequestDto request) {
        log.debug("Creating training request, trainee username {}, trainer username {}",
                request.getTraineeUsername(), request.getTrainerUsername());

        var trainee = findTraineeByUsername(request.getTraineeUsername());
        var trainer = findTrainerByUsername(request.getTrainerUsername());

        var training = trainingMapper.toEntity(request);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainer.getSpecialization());

        var savedTraining = trainingRepository.save(training);

        log.info("Training created successfully, id {}, trainee username {}, trainer username {}",
                savedTraining.getId(),
                savedTraining.getTrainee().getUser().getUsername(),
                savedTraining.getTrainer().getUser().getUsername());

        return trainingMapper.toResponseDto(savedTraining);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponseDto> findAll(TrainingSearchCriteria criteria) {
        log.debug("Searching trainings with criteria {}", criteria);

        var trainings = trainingRepository
                .findAll(TrainingSpecification.byCriteria(criteria))
                .stream()
                .map(trainingMapper::toResponseDto)
                .toList();

        log.info("Trainings found, count {}", trainings.size());

        return trainings;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting training by id {}", id);

        if (!trainingRepository.existsById(id)) {
            log.warn("Training delete failed because training not found, id {}", id);
            throw new EntityNotFoundException("Training not found");
        }

        trainingRepository.deleteById(id);

        log.info("Training deleted successfully, id {}", id);
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
