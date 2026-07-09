package com.example.core.service.impl;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.exception.AuthenticationException;
import com.example.core.mapper.AuthMapper;
import com.example.core.mapper.TraineeMapper;
import com.example.core.mapper.TrainerMapper;
import com.example.core.mapper.TrainingMapper;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.User;
import com.example.core.repository.TraineeRepository;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.repository.UserRepository;
import com.example.core.service.TraineeService;
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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;
    private final TrainingMapper trainingMapper;
    private final AuthMapper authMapper;

    private final UsernameGenerator usernameGenerator;

    @Override
    @Transactional
    public CreatedProfileResponseDto create(CreateTraineeRequestDto request) {
        log.debug("Create trainee profile for first name {}, last name {}",
                request.getFirstName(), request.getLastName());


        var username = usernameGenerator.generate(request.getFirstName(), request.getLastName());
        var password = PasswordGenerator.generatePassword();

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(username)
                .password(password)
                .isActive(true)
                .build();

        var trainee = traineeMapper.toEntity(request);
        trainee.setUser(user);

        var savedTrainee = traineeRepository.save(trainee);

        log.info("Trainee profile created successfully, username {}", savedTrainee.getUser().getUsername());

        return authMapper.toCreateProfileResponseDto(savedTrainee.getUser());
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeResponseDto findByUsername(String username) {
        log.debug("Searching trainee profile by username {}", username);

        var trainee = findTraineeByUsername(username);

        log.info("Trainee profile found successfully, username {}", username);

        return traineeMapper.toResponseDto(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeResponseDto> findAll() {
        log.debug("Searching trainee profiles");

        var trainees = traineeRepository.findAll()
                .stream().map(traineeMapper::toResponseDto).toList();

        log.info("Trainee profiles found, count {}", trainees.size());

        return trainees;
    }

    @Override
    @Transactional
    public TraineeResponseDto update(String username, UpdateTraineeRequestDto request) {
        log.debug("Update trainee profile for username {}", username);

        var trainee = findTraineeByUsername(username);

        traineeMapper.updateEntity(request, trainee);

        var updatedTrainee = traineeRepository.save(trainee);
        log.info("Trainee profile updated successfully, username {}", username);

        return traineeMapper.toResponseDto(updatedTrainee);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequestDto request) {
        log.debug("Change password for username {}", username);

        var trainee = findTraineeByUsername(username);

        if (!userRepository.existsByUsernameAndPassword(username, request.getOldPassword())) {
            log.warn("Trainee password change failed due to invalid current password, username {}", username);
            throw new AuthenticationException("Invalid current password");
        }

        trainee.getUser().setPassword(request.getPassword());
        traineeRepository.save(trainee);
        log.info("Trainee password changed successfully, username {}", username);
    }

    @Override
    @Transactional
    public void changeStatus(String username, boolean active) {
        log.debug("Changing trainee status, username {}, active {}",
                username, active);

        var trainee = findTraineeByUsername(username);
        var user = trainee.getUser();

        if (user.isActive() == active) {
            log.warn("Trainee status change failed because status is already {}, username {}",
                    active, username);
            throw new IllegalStateException("Trainee already has this status");
        }

        user.setActive(active);
        traineeRepository.save(trainee);

        log.info("Trainee status changed successfully, username {}, active {}",
                username, active);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        log.debug("Delete trainee profile for username {}", username);

        var trainee = findTraineeByUsername(username);

        trainingRepository.deleteByTraineeUserUsername(username);
        traineeRepository.delete(trainee);

        log.info("Trainee profile deleted successfully, username {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponseDto> getTrainings(String username, TrainingSearchCriteria criteria) {
        log.debug("Searching trainee profile for username {}, criteria {}", username, criteria);

        findTraineeByUsername(username);
        criteria.setTraineeUsername(username);

        var trainings = trainingRepository
                .findAll(TrainingSpecification.byCriteria(criteria))
                .stream()
                .map(trainingMapper::toResponseDto)
                .toList();

        log.info("Trainings found for trainee username {}, count {}", username, trainings.size());

        return trainings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponseDto> getNotAssignedTrainers(String username) {
        log.debug("Searching trainers not assigned to trainee username {}", username);

        var trainee = findTraineeByUsername(username);

        var trainers = trainerRepository.findAll()
                .stream()
                .filter(trainer -> !trainee.getTrainers().contains(trainer))
                .map(trainerMapper::toResponseDto)
                .toList();

        log.info("Not assigned trainers found for trainee username {}, count {}", username, trainers.size());

        return trainers;
    }

    @Override
    @Transactional
    public TraineeResponseDto updateTrainers(String username, Set<String> trainerUsernames) {
        log.debug("Updating trainers list for username {}, trainers count {}",
                username, trainerUsernames.size());

        var trainee = findTraineeByUsername(username);

        var trainers = trainerUsernames.stream()
                .map(this::findTrainerByUsername)
                .collect(Collectors.toSet());

        trainee.setTrainers(trainers);

        var updatedTrainee = traineeRepository.save(trainee);

        log.info("Trainee trainers list updated successfully, username {}, trainers count {}",
                username, trainers.size());

        return traineeMapper.toResponseDto(updatedTrainee);
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
