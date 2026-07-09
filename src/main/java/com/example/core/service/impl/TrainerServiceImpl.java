package com.example.core.service.impl;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.exception.AuthenticationException;
import com.example.core.mapper.AuthMapper;
import com.example.core.mapper.TrainerMapper;
import com.example.core.mapper.TrainingMapper;
import com.example.core.model.Trainer;
import com.example.core.model.TrainingType;
import com.example.core.model.User;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.repository.TrainingTypeRepository;
import com.example.core.repository.UserRepository;
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
    private final UserRepository userRepository;

    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final AuthMapper authMapper;

    private final UsernameGenerator usernameGenerator;

    @Override
    @Transactional
    public CreatedProfileResponseDto create(CreateTrainerRequestDto request) {
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

        var trainer = trainerMapper.toEntity(request);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);

        var savedTrainer = trainerRepository.save(trainer);

        log.info("Trainer profile create successfully, username {}, specialization {}",
                savedTrainer.getUser().getUsername(),
                savedTrainer.getSpecialization().getTrainingTypeName());

        return authMapper.toCreateProfileResponseDto(savedTrainer.getUser());
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerResponseDto findByUsername(String username) {
        log.debug("Finding trainer profile by username {}", username);

        var trainer = findTrainerByUsername(username);

        log.info("Trainer profile found successfully, username {}", username);

        return trainerMapper.toResponseDto(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponseDto> findAll() {
        log.debug("Searching all trainers profile");

        var trainers = trainerRepository.findAll()
                .stream()
                .map(trainerMapper::toResponseDto)
                .toList();

        log.info("Trainers found successfully, count {}", trainers.size());
        return trainers;
    }

    @Override
    @Transactional
    public TrainerResponseDto update(String username, UpdateTrainerRequestDto request) {
        log.debug("Updating trainer profile for username {}, specialization {}",
                username, request.getSpecialization());

        var trainer = findTrainerByUsername(username);
        var specialization = findTrainingTypeByName(request.getSpecialization());

        trainerMapper.updateEntity(request, trainer);
        trainer.setSpecialization(specialization);

        var updatedTrainer = trainerRepository.save(trainer);

        log.info("Trainer profile updated successfully, username {}", username);
        return trainerMapper.toResponseDto(updatedTrainer);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequestDto request) {
        log.debug("Changing trainer password, username {}", username);

        var trainer = findTrainerByUsername(username);

        boolean passwordMatches = userRepository.existsByUsernameAndPassword(username, request.getOldPassword());
        if (!passwordMatches) {
            log.warn("Trainer password change failed due to invalid current password, username {}", username);
            throw new AuthenticationException("Invalid current password");
        }

        trainer.getUser().setPassword(request.getPassword());
        trainerRepository.save(trainer);

        log.info("Trainer password change successfully, username {}", username);
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
    public List<TrainingResponseDto> getTrainings(String username, TrainingSearchCriteria criteria) {
        log.debug("Searching trainings for trainer username {}, criteria {}", username, criteria);

        findTrainerByUsername(username);
        criteria.setTrainerUsername(username);

        var trainings = trainingRepository
                .findAll(TrainingSpecification.byCriteria(criteria))
                .stream()
                .map(trainingMapper::toResponseDto)
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
