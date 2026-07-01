package com.example.core.service;

import com.example.core.dao.TrainingDao;
import com.example.core.model.Training;
import com.example.core.model.TrainingType;
import com.example.core.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void shouldCreateTrainingSuccessfully() {
        var training = createTraining(null);
        var savedTraining = createTraining(1L);

        when(trainingDao.save(training)).thenReturn(savedTraining);

        var result = trainingService.create(training);

        assertEquals(savedTraining, result, "Created training should match saved training");

        verify(trainingDao).save(training);
    }

    @Test
    void shouldThrowExceptionWhenCreatingNullTraining() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(null),
                "Creating null training should throw IllegalArgumentException"
        );

        assertEquals("Training cannot be null", exception.getMessage(), "Exception message should describe null training validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTraineeIdIsNull() {
        var training = createTraining(null);
        training.setTraineeId(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training without trainee id should throw IllegalArgumentException"
        );

        assertEquals("Training trainee id cannot be null", exception.getMessage(), "Exception message should describe missing trainee id validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTrainerIdIsNull() {
        var training = createTraining(null);
        training.setTrainerId(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training without trainer id should throw IllegalArgumentException"
        );

        assertEquals("Training trainer id cannot be null", exception.getMessage(), "Exception message should describe missing trainer id validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTrainingNameIsNull() {
        var training = createTraining(null);
        training.setTrainingName(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training with null name should throw IllegalArgumentException"
        );

        assertEquals("Training name cannot be empty", exception.getMessage(), "Exception message should describe empty training name validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTrainingNameIsBlank() {
        var training = createTraining(null);
        training.setTrainingName(" ");

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training with blank name should throw IllegalArgumentException"
        );

        assertEquals("Training name cannot be empty", exception.getMessage(), "Exception message should describe empty training name validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTrainingTypeIsNull() {
        var training = createTraining(null);
        training.setTrainingType(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training without type should throw IllegalArgumentException"
        );

        assertEquals("Training type cannot be null", exception.getMessage(), "Exception message should describe missing training type validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTrainingDateIsNull() {
        var training = createTraining(null);
        training.setTrainingDate(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training without date should throw IllegalArgumentException"
        );

        assertEquals("Training date cannot be null", exception.getMessage(), "Exception message should describe missing training date validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTrainingDurationIsNull() {
        var training = createTraining(null);
        training.setTrainingDuration(null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training without duration should throw IllegalArgumentException"
        );

        assertEquals("Training duration cannot be null", exception.getMessage(), "Exception message should describe missing training duration validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTrainingDurationIsZero() {
        var training = createTraining(null);
        training.setTrainingDuration(0);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training with zero duration should throw IllegalArgumentException"
        );

        assertEquals("Training duration must be positive", exception.getMessage(), "Exception message should describe non-positive training duration validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldThrowExceptionWhenTrainingDurationIsNegative() {
        var training = createTraining(null);
        training.setTrainingDuration(-10);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingService.create(training),
                "Creating training with negative duration should throw IllegalArgumentException"
        );

        assertEquals("Training duration must be positive", exception.getMessage(), "Exception message should describe non-positive training duration validation");

        verifyNoInteractions(trainingDao);
    }

    @Test
    void shouldGetTrainingByIdWhenTrainingExists() {
        var training = createTraining(1L);

        when(trainingDao.findById(1L)).thenReturn(Optional.of(training));

        var result = trainingService.getTrainingById(1L);

        assertTrue(result.isPresent(), "Training should be found by existing id");
        assertEquals(training, result.get(), "Found training should match expected training");

        verify(trainingDao).findById(1L);
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainingDoesNotExist() {
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());

        var result = trainingService.getTrainingById(99L);

        assertTrue(result.isEmpty(), "Result should be empty when training id does not exist");

        verify(trainingDao).findById(99L);
    }

    @Test
    void shouldFindAllTrainings() {
        var trainings = List.of(
                createTraining(1L),
                createTraining(2L)
        );

        when(trainingDao.findAll()).thenReturn(trainings);

        var result = trainingService.findAll();

        assertEquals(2, result.size(), "Result should contain two trainings");
        assertEquals(trainings, result, "Result should match expected training list");

        verify(trainingDao).findAll();
    }

    private Training createTraining(Long id) {
        return Training.builder()
                .id(id)
                .traineeId(1L)
                .trainerId(1L)
                .trainingName("Morning Workout")
                .trainingType(
                        TrainingType.builder()
                                .id(1L)
                                .trainingTypeName("Fitness")
                                .build()
                )
                .trainingDate(LocalDate.of(2026, 6, 27))
                .trainingDuration(60)
                .build();
    }
}
