package com.example.core.service.impl;

import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.TrainingType;
import com.example.core.model.User;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.repository.TrainingTypeRepository;
import com.example.core.service.TrainerService;
import com.example.core.service.util.PasswordGenerator;
import com.example.core.service.util.UsernameGenerator;
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
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UsernameGenerator usernameGenerator;

    @Override
    @Transactional
    public User create(CreateTrainerRequestDto request) {
        log.debug("Creating trainer profile for first name {}, last name {}",
                request.getFirstName(), request.getLastName());

        var specialization = findTrainingTypeByName(request.getSpecialization());

        var username = usernameGenerator.generate(request.getFirstName(), request.getLastName());
        var password = PasswordGenerator.generatePassword();

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(username)
                .password(password)
                .isActive(true)
                .build();

        var trainer = Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();

        var savedTrainer = trainerRepository.save(trainer);

        log.info("Trainer profile create successfully, username {}, specialization {}",
                savedTrainer.getUser().getUsername(),
                savedTrainer.getSpecialization().getTrainingTypeName());

        return savedTrainer.getUser();
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findByUsername(String username) {
        log.debug("Finding trainer profile by username {}", username);

        var trainer = findTrainerByUsername(username);

        log.info("Trainer profile found successfully, username {}", username);

        return trainer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        log.debug("Searching all trainers profile");

        var trainers = trainerRepository.findAll();

        log.info("Trainers found successfully, count {}", trainers.size());
        return trainers;
    }

    @Override
    @Transactional
    public Trainer update(String username, UpdateTrainerRequestDto request) {
        log.debug("Updating trainer profile for username {}", username);

        var trainer = findTrainerByUsername(username);

        trainer.getUser().setFirstName(request.getFirstName());
        trainer.getUser().setLastName(request.getLastName());

        var updatedTrainer = trainerRepository.save(trainer);

        log.info("Trainer profile updated successfully, username {}", username);
        return updatedTrainer;
    }

    @Override
    @Transactional
    public void changeStatus(String username, boolean active) {
        log.debug("Changing trainer status for username {}, active {}", username, active);

        var trainer = findTrainerByUsername(username);
        var user = trainer.getUser();

        if (user.isActive() == active) {
            log.warn("Trainer status change failed because status is already {}, username {}",
                    active, username);
            throw new IllegalStateException("Trainer already has this status");
        }

        user.setActive(active);
        trainerRepository.save(trainer);

        log.info("Trainer status change successfully, username {}, status {}", username, active);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        log.debug("Deleting trainer profile for username {}", username);

        var trainer = findTrainerByUsername(username);
        trainerRepository.delete(trainer);

        log.info("Trainer profile delete successfully, username {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, TrainingSearchCriteria criteria) {
        log.debug("Searching trainings for trainer username {}, criteria {}", username, criteria);

        findTrainerByUsername(username);
        criteria.setTrainerUsername(username);

        var trainings = trainingRepository
                .findAll(TrainingSpecification.byCriteria(criteria))
                .stream()
                .toList();

        log.info("Trainings found for trainer username {}, count {}", username, trainings.size());

        return trainings;
    }

    private Trainer findTrainerByUsername(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer profile not found, username {}", username);
                    return new EntityNotFoundException("Trainer profile not found");
                });
    }

    private TrainingType findTrainingTypeByName(String specialization) {
        return trainingTypeRepository.findByTrainingTypeName(specialization)
                .orElseThrow(() -> {
                    log.warn("Training type not found, name {}", specialization);
                    return new EntityNotFoundException("Training type not found");
                });
    }
}
