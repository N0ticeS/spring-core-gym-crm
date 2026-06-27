package com.example.core.storage;

import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StorageBeanPostProcessorTest {

    private StorageBeanPostProcessor postProcessor;
    private StorageDataLoader storageDataLoader;
    private StorageData storageData;

    @BeforeEach
    void setUp() {
        storageDataLoader = mock(StorageDataLoader.class);
        postProcessor = new StorageBeanPostProcessor(storageDataLoader);

        storageData = new StorageData();
        storageData.getTrainees().add(createTrainee(1L));
        storageData.getTrainers().add(createTrainer(1L));
        storageData.getTrainings().add(createTraining(1L));

        when(storageDataLoader.load()).thenReturn(storageData);
    }

    @Test
    void shouldFillTraineeStorage() {
        var traineeStorage = new ConcurrentHashMap<Long, Trainee>();

        var result = postProcessor.postProcessAfterInitialization(
                traineeStorage,
                "traineeStorage"
        );

        assertSame(traineeStorage, result, "Post processor should return the same trainee storage bean");
        assertEquals(1, traineeStorage.size(), "Trainee storage should contain one trainee");
        assertEquals("John.Smith", traineeStorage.get(1L).getUsername(), "Stored trainee username should match expected value");

        verify(storageDataLoader).load();
    }

    @Test
    void shouldFillTrainerStorage() {
        var trainerStorage = new ConcurrentHashMap<Long, Trainer>();

        var result = postProcessor.postProcessAfterInitialization(
                trainerStorage,
                "trainerStorage"
        );

        assertSame(trainerStorage, result, "Post processor should return the same trainer storage bean");
        assertEquals(1, trainerStorage.size(), "Trainer storage should contain one trainer");
        assertEquals("Fitness", trainerStorage.get(1L).getSpecialization(), "Stored trainer specialization should match expected value");

        verify(storageDataLoader).load();
    }

    @Test
    void shouldFillTrainingStorage() {
        var trainingStorage = new ConcurrentHashMap<Long, Training>();

        var result = postProcessor.postProcessAfterInitialization(
                trainingStorage,
                "trainingStorage"
        );

        assertSame(trainingStorage, result, "Post processor should return the same training storage bean");
        assertEquals(1, trainingStorage.size(), "Training storage should contain one training");
        assertEquals("Morning Workout", trainingStorage.get(1L).getTrainingName(), "Stored training name should match expected value");

        verify(storageDataLoader).load();
    }

    @Test
    void shouldIgnoreNonMapBean() {
        var bean = "not a map bean";

        var result = postProcessor.postProcessAfterInitialization(
                bean,
                "traineeStorage"
        );

        assertSame(bean, result, "Post processor should return the same non-map bean");

        verifyNoInteractions(storageDataLoader);
    }

    @Test
    void shouldIgnoreMapWithUnsupportedBeanName() {
        var unsupportedStorage = new ConcurrentHashMap<Long, Trainee>();

        var result = postProcessor.postProcessAfterInitialization(
                unsupportedStorage,
                "unknownStorage"
        );

        assertSame(unsupportedStorage, result, "Post processor should return the same unsupported storage bean");
        assertTrue(unsupportedStorage.isEmpty(), "Unsupported storage should remain empty");

        verifyNoInteractions(storageDataLoader);
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
                .firstName("Robert")
                .lastName("Johnson")
                .username("Robert.Johnson")
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
