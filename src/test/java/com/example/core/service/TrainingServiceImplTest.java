package com.example.core.service;

import com.example.core.converter.CreateTrainingRequestToTrainingConverter;
import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.dto.workload.ActionType;
import com.example.core.dto.workload.TrainerWorkloadRequestDto;
import com.example.core.messaging.producer.TrainerWorkloadProducer;
import com.example.core.metrics.TrainingCreationMetrics;
import com.example.core.model.*;
import com.example.core.repository.TraineeRepository;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.service.impl.TrainingServiceImpl;
import com.example.core.specification.TrainingSearchCriteria;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private CreateTrainingRequestToTrainingConverter createTrainingConverter;

    @Mock
    private TrainingCreationMetrics trainingCreationMetrics;

    @Mock
    private TrainerWorkloadProducer trainerWorkloadProducer;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void shouldCreateTrainingSuccessfully() {
        CreateTrainingRequestDto request = createTrainingRequest();

        Trainee trainee = createTrainee("John.Smith");
        Trainer trainer = createTrainer("Mike.Brown", "Fitness");
        Training training = createTraining();

        Training savedTraining = createTraining();
        savedTraining.setId(1L);
        savedTraining.setTrainee(trainee);
        savedTraining.setTrainer(trainer);
        savedTraining.setTrainingType(trainer.getSpecialization());

        when(traineeRepository.findByUserUsername("John.Smith"))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findByUserUsername("Mike.Brown"))
                .thenReturn(Optional.of(trainer));

        when(createTrainingConverter.convert(request))
                .thenReturn(training);

        when(trainingRepository.save(training))
                .thenReturn(savedTraining);

        Training result = trainingService.createTraining(request);

        assertEquals(
                "Morning Fitness",
                result.getTrainingName(),
                "Training name should match"
        );

        assertEquals(
                trainee,
                training.getTrainee(),
                "Trainee should be assigned to training"
        );

        assertEquals(
                trainer,
                training.getTrainer(),
                "Trainer should be assigned to training"
        );

        assertEquals(
                trainer.getSpecialization(),
                training.getTrainingType(),
                "Training type should match trainer specialization"
        );

        ArgumentCaptor<TrainerWorkloadRequestDto> workloadCaptor =
                ArgumentCaptor.forClass(TrainerWorkloadRequestDto.class);

        verify(trainerWorkloadProducer)
                .send(workloadCaptor.capture());

        TrainerWorkloadRequestDto workloadRequest =
                workloadCaptor.getValue();

        assertEquals(
                "Mike.Brown",
                workloadRequest.getTrainerUsername(),
                "Trainer username should match"
        );

        assertEquals(
                "Mike",
                workloadRequest.getTrainerFirstName(),
                "Trainer first name should match"
        );

        assertEquals(
                "Brown",
                workloadRequest.getTrainerLastName(),
                "Trainer last name should match"
        );

        assertEquals(
                LocalDate.of(2026, 7, 10),
                workloadRequest.getTrainingDate(),
                "Training date should match"
        );

        assertEquals(
                60,
                workloadRequest.getTrainingDuration(),
                "Training duration should match"
        );

        assertEquals(
                ActionType.ADD,
                workloadRequest.getActionType(),
                "Action type should be ADD"
        );

        verify(trainingRepository).save(training);
        verify(createTrainingConverter).convert(request);
        verify(trainingCreationMetrics).increment();
    }

    @Test
    void shouldThrowExceptionWhenTraineeNotFoundOnCreateTraining() {
        CreateTrainingRequestDto request = createTrainingRequest();

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.createTraining(request)
        );

        assertEquals("Trainee profile not found", exception.getMessage(), "Exception message should match");

        verify(trainerRepository, never()).findByUserUsername(anyString());
        verify(trainingRepository, never()).save(any(Training.class));
        verifyNoInteractions(createTrainingConverter);
        verifyNoInteractions(trainerWorkloadProducer);
    }

    @Test
    void shouldThrowExceptionWhenTrainerNotFoundOnCreateTraining() {
        CreateTrainingRequestDto request = createTrainingRequest();

        Trainee trainee = createTrainee("John.Smith");

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("Mike.Brown")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.createTraining(request)
        );

        assertEquals("Trainer profile not found", exception.getMessage(), "Exception message should match");

        verify(trainingRepository, never()).save(any(Training.class));
        verifyNoInteractions(createTrainingConverter);
        verifyNoInteractions(trainerWorkloadProducer);
    }

    @Test
    void shouldFindAllTrainingsSuccessfully() {
        TrainingSearchCriteria criteria = new TrainingSearchCriteria();

        Training trainingOne = createTraining();
        Training trainingTwo = createTraining();
        trainingTwo.setTrainingName("Evening Yoga");

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(trainingOne, trainingTwo));

        List<Training> result = trainingService.findAll(criteria);

        assertEquals(2, result.size(), "Two trainings should be returned");
        assertEquals("Morning Fitness", result.get(0).getTrainingName(), "First training name should match");
        assertEquals("Evening Yoga", result.get(1).getTrainingName(), "Second training name should match");

        verify(trainingRepository).findAll(any(Specification.class));
    }

    @Test
    void shouldDeleteTrainingSuccessfully() {
        Long trainingId = 1L;

        Trainer trainer = createTrainer("Mike.Brown", "Fitness");

        Training training = Training.builder()
                .id(trainingId)
                .trainingName("Future Fitness")
                .trainingDate(LocalDate.now().plusDays(5))
                .trainingDuration(90)
                .trainer(trainer)
                .trainingType(trainer.getSpecialization())
                .build();

        when(trainingRepository.findById(trainingId))
                .thenReturn(Optional.of(training));

        trainingService.deleteTraining(trainingId);

        ArgumentCaptor<TrainerWorkloadRequestDto> workloadCaptor =
                ArgumentCaptor.forClass(TrainerWorkloadRequestDto.class);

        verify(trainerWorkloadProducer)
                .send(workloadCaptor.capture());

        TrainerWorkloadRequestDto workloadRequest =
                workloadCaptor.getValue();

        assertEquals(
                "Mike.Brown",
                workloadRequest.getTrainerUsername(),
                "Trainer username should match"
        );

        assertEquals(
                90,
                workloadRequest.getTrainingDuration(),
                "Training duration should match"
        );

        assertEquals(
                training.getTrainingDate(),
                workloadRequest.getTrainingDate(),
                "Training date should match"
        );

        assertEquals(
                ActionType.DELETE,
                workloadRequest.getActionType(),
                "Action type should be DELETE"
        );

        verify(trainingRepository)
                .delete(training);
    }

    @Test
    void shouldThrowExceptionWhenTrainingNotFoundOnDelete() {
        Long trainingId = 999L;

        when(trainingRepository.findById(trainingId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainingService.deleteTraining(trainingId),
                "EntityNotFoundException should be thrown when training is not found"
        );

        assertEquals(
                "Training with id 999 not found",
                exception.getMessage(),
                "Exception message should match"
        );

        verify(trainingRepository, never())
                .delete(any(Training.class));

        verifyNoInteractions(trainerWorkloadProducer);
    }

    @Test
    void shouldThrowExceptionWhenDeletingPastTraining() {
        Long trainingId = 1L;

        Training training = Training.builder()
                .id(trainingId)
                .trainingName("Past Fitness")
                .trainingDate(LocalDate.now().minusDays(1))
                .trainingDuration(60)
                .build();

        when(trainingRepository.findById(trainingId))
                .thenReturn(Optional.of(training));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> trainingService.deleteTraining(trainingId),
                "IllegalStateException should be thrown when deleting past training"
        );

        assertEquals(
                "Past training cannot be deleted, id: 1",
                exception.getMessage(),
                "Exception message should match"
        );

        verify(trainingRepository, never())
                .delete(any(Training.class));

        verifyNoInteractions(trainerWorkloadProducer);
    }

    private CreateTrainingRequestDto createTrainingRequest() {
        CreateTrainingRequestDto request = new CreateTrainingRequestDto();
        request.setTrainingName("Morning Fitness");
        request.setTrainingDate(LocalDate.of(2026, 7, 10));
        request.setTrainingDuration(60);
        request.setTraineeUsername("John.Smith");
        request.setTrainerUsername("Mike.Brown");
        return request;
    }

    private Training createTraining() {
        return Training.builder()
                .trainingName("Morning Fitness")
                .trainingDate(LocalDate.of(2026, 7, 10))
                .trainingDuration(60)
                .build();
    }

    private Trainee createTrainee(String username) {
        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username(username)
                .password("password123")
                .isActive(true)
                .build();

        return Trainee.builder()
                .user(user)
                .build();
    }

    private Trainer createTrainer(String username, String specializationName) {
        User user = User.builder()
                .firstName("Mike")
                .lastName("Brown")
                .username(username)
                .password("password123")
                .isActive(true)
                .build();

        TrainingType specialization = TrainingType.builder()
                .id(1L)
                .trainingTypeName(specializationName)
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }
}
