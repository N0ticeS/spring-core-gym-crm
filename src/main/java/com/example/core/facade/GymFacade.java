package com.example.core.facade;

import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.service.TrainingService;
import com.example.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class GymFacade {

    private final UserService<Trainee> traineeService;
    private final TrainingService trainingService;
    private final UserService<Trainer> trainerService;

    public Trainee createTrainee(Trainee trainee) {
        log.debug("Facade request: create trainee");
        return traineeService.create(trainee);
    }

    public boolean updateTrainee(Trainee trainee) {
        log.debug("Facade request: update trainee");
        return traineeService.update(trainee);
    }

    public boolean deleteTrainee(Long id) {
        log.debug("Facade request: delete trainee by id={}", id);
        return traineeService.delete(id);
    }

    public Optional<Trainee> findTraineeById(Long id) {
        log.debug("Facade request: find trainee by id={}", id);
        return traineeService.findById(id);
    }

    public Optional<Trainee> findTraineeByUsername(String username) {
        log.debug("Facade request: find trainee by username={}", username);
        return traineeService.findByUsername(username);
    }

    public List<Trainee> findAllTrainees() {
        log.debug("Facade request: find all trainees");
        return traineeService.findAll();
    }

    public Trainer createTrainer(Trainer trainer) {
        log.debug("Facade request: create trainer");
        return trainerService.create(trainer);
    }

    public boolean updateTrainer(Trainer trainer) {
        log.debug("Facade request: update trainer");
        return trainerService.update(trainer);
    }

    public Optional<Trainer> findTrainerById(Long id) {
        log.debug("Facade request: find trainer by id={}", id);
        return trainerService.findById(id);
    }

    public Optional<Trainer> findTrainerByUsername(String username) {
        log.debug("Facade request: find trainer by username={}", username);
        return trainerService.findByUsername(username);
    }

    public List<Trainer> findAllTrainers() {
        log.debug("Facade request: find all trainers");
        return trainerService.findAll();
    }

    public Training createTraining(Training training) {
        log.debug("Facade request: create training");
        return trainingService.create(training);
    }

    public Optional<Training> findTrainingById(Long id) {
        log.debug("Facade request: find training by id={}", id);
        return trainingService.getTrainingById(id);
    }

    public List<Training> findAllTrainings() {
        log.debug("Facade request: find all trainings");
        return trainingService.findAll();
    }
}
