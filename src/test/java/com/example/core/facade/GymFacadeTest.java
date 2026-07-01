package com.example.core.facade;

import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.TrainingType;
import com.example.core.service.TrainingService;
import com.example.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GymFacadeTest {

    @Mock
    private UserService<Trainee> traineeService;

    @Mock
    private UserService<Trainer> trainerService;

    @Mock
    private TrainingService trainingService;

    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        gymFacade = new GymFacade(
                traineeService,
                trainingService,
                trainerService
        );
    }


    @Test
    void shouldCreateTrainee() {
        var trainee = createTrainee(1L);

        when(traineeService.create(trainee)).thenReturn(trainee);

        var result = gymFacade.createTrainee(trainee);

        assertEquals(trainee, result, "Created trainee should match expected trainee");
        verify(traineeService).create(trainee);
    }

    @Test
    void shouldUpdateTrainee() {
        var trainee = createTrainee(1L);

        when(traineeService.update(trainee)).thenReturn(true);

        var result = gymFacade.updateTrainee(trainee);

        assertTrue(result, "Trainee update should return true");
        verify(traineeService).update(trainee);
    }

    @Test
    void shouldDeleteTrainee() {
        when(traineeService.delete(1L)).thenReturn(true);

        var result = gymFacade.deleteTrainee(1L);

        assertTrue(result, "Trainee delete should return true");
        verify(traineeService).delete(1L);
    }

    @Test
    void shouldFindTraineeById() {
        var trainee = createTrainee(1L);

        when(traineeService.findById(1L)).thenReturn(Optional.of(trainee));

        var result = gymFacade.findTraineeById(1L);

        assertTrue(result.isPresent(), "Trainee should be found by existing id");
        assertEquals(trainee, result.get(), "Found trainee should match expected trainee");
        verify(traineeService).findById(1L);
    }

    @Test
    void shouldFindTraineeByUsername() {
        var trainee = createTrainee(1L);

        when(traineeService.findByUsername("John.Smith"))
                .thenReturn(Optional.of(trainee));

        var result = gymFacade.findTraineeByUsername("John.Smith");

        assertTrue(result.isPresent(), "Trainee should be found by existing username");
        assertEquals(trainee, result.get(), "Found trainee should match expected username owner");
        verify(traineeService).findByUsername("John.Smith");
    }

    @Test
    void shouldFindAllTrainees() {
        var trainees = List.of(createTrainee(1L), createTrainee(2L));

        when(traineeService.findAll()).thenReturn(trainees);

        var result = gymFacade.findAllTrainees();

        assertEquals(2, result.size(), "Result should contain two trainees");
        assertEquals(trainees, result, "Result should match expected trainee list");

        verify(traineeService).findAll();
    }

    @Test
    void shouldCreateTrainer() {
        var trainer = createTrainer(1L);

        when(trainerService.create(trainer)).thenReturn(trainer);

        var result = gymFacade.createTrainer(trainer);

        assertEquals(trainer, result, "Created trainer should match expected trainer");
        verify(trainerService).create(trainer);
    }

    @Test
    void shouldUpdateTrainer() {
        var trainer = createTrainer(1L);

        when(trainerService.update(trainer)).thenReturn(true);

        var result = gymFacade.updateTrainer(trainer);

        assertTrue(result, "Trainer update should return true");
        verify(trainerService).update(trainer);
    }

    @Test
    void shouldFindTrainerById() {
        var trainer = createTrainer(1L);

        when(trainerService.findById(1L)).thenReturn(Optional.of(trainer));

        var result = gymFacade.findTrainerById(1L);

        assertTrue(result.isPresent(), "Trainer should be found by existing id");
        assertEquals(trainer, result.get(), "Found trainer should match expected trainer");
        verify(trainerService).findById(1L);
    }

    @Test
    void shouldFindTrainerByUsername() {
        var trainer = createTrainer(1L);

        when(trainerService.findByUsername("John.Smith"))
                .thenReturn(Optional.of(trainer));

        var result = gymFacade.findTrainerByUsername("John.Smith");

        assertTrue(result.isPresent(), "Trainer should be found by existing username");
        assertEquals(trainer, result.get(), "Found trainer should match expected username owner");

        verify(trainerService).findByUsername("John.Smith");
    }

    @Test
    void shouldFindAllTrainers() {
        var trainers = List.of(createTrainer(1L), createTrainer(2L));

        when(trainerService.findAll()).thenReturn(trainers);

        var result = gymFacade.findAllTrainers();

        assertEquals(2, result.size(), "Result should contain two trainers");
        assertEquals(trainers, result, "Result should match expected trainer list");
        verify(trainerService).findAll();
    }

    @Test
    void shouldCreateTraining() {
        var training = createTraining(1L);

        when(trainingService.create(training)).thenReturn(training);

        var result = gymFacade.createTraining(training);

        assertEquals(training, result, "Created training should match expected training");
        verify(trainingService).create(training);
    }

    @Test
    void shouldFindTrainingById() {
        var training = createTraining(1L);

        when(trainingService.getTrainingById(1L))
                .thenReturn(Optional.of(training));

        var result = gymFacade.findTrainingById(1L);

        assertTrue(result.isPresent(), "Training should be found by existing id");
        assertEquals(training, result.get(), "Found training should match expected training");
        verify(trainingService).getTrainingById(1L);
    }

    @Test
    void shouldFindAllTrainings() {
        var trainings = List.of(createTraining(1L), createTraining(2L));

        when(trainingService.findAll()).thenReturn(trainings);

        var result = gymFacade.findAllTrainings();

        assertEquals(2, result.size(), "Result should contain two trainings");
        assertEquals(trainings, result, "Result should match expected training list");
        verify(trainingService).findAll();
    }

    private Trainee createTrainee(Long id) {
        return Trainee.builder()
                .id(id)
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("Password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("New York")
                .build();
    }

    private Trainer createTrainer(Long id) {
        return Trainer.builder()
                .id(id)
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("Password123")
                .isActive(true)
                .specialization("Fitness")
                .build();
    }

    private Training createTraining(Long id) {
        return Training.builder()
                .id(id)
                .traineeId(1L)
                .trainerId(1L)
                .trainingName("Morning Workout")
                .trainingType(TrainingType.builder()
                        .id(1L)
                        .trainingTypeName("Fitness")
                        .build())
                .trainingDate(LocalDate.of(2026, 6, 27))
                .trainingDuration(60)
                .build();
    }
}
