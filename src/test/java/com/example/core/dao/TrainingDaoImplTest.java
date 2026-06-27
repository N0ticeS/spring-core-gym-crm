package com.example.core.dao;

import com.example.core.dao.impl.TrainingDaoImpl;
import com.example.core.model.Training;
import com.example.core.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingDaoImplTest {

    Map<Long, Training> storage;
    private TrainingDaoImpl trainingDao;

    @BeforeEach
    void setup() {
        storage = new ConcurrentHashMap<>();

        trainingDao = new TrainingDaoImpl(storage);
    }

    @Test
    void shouldSaveTrainingWithGeneratedIdWheIdIsNull() {
        var training = createTraining(null);

        var savedTraining = trainingDao.save(training);

        assertNotNull(savedTraining.getId(), "Training id should not be null");
        assertEquals(1L, savedTraining.getId(), "Generated training id should be 1");
        assertEquals(savedTraining, storage.get(1L), "Saved training should be stored by generated id");
    }

    @Test
    void shouldSaveTrainingWithExistingId() {
        var training = createTraining(1L);

        var savedTraining = trainingDao.save(training);

        assertNotNull(savedTraining.getId(), "Training id should not be null");
        assertEquals(1L, savedTraining.getId(), "Existing training id should be preserved");
        assertEquals(savedTraining, storage.get(1L), "Saved training should be stored by existing id");
    }

    @Test
    void shouldGenerateNextIdBasedOnMaxExistingId() {
        storage.put(1L, createTraining(1L));
        storage.put(10L, createTraining(10L));

        var training = createTraining(null);

        var savedTraining = trainingDao.save(training);

        assertEquals(
                11L,
                savedTraining.getId(),
                "Generated training id should be one greater than max existing id"
        );
    }

    @Test
    void shouldThrowExceptionWhenSavingNullTraining() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingDao.save(null)
        );

        assertEquals(
                "Training cannot be null",
                exception.getMessage(),
                "Exception message should describe the validation error"
        );
    }

    @Test
    void shouldThrowExceptionWhenTrainingWithSameIdAlreadyExists() {
        storage.put(1L, createTraining(1L));

        var duplicate = createTraining(1L);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainingDao.save(duplicate),
                "Saving training with duplicate id should throw IllegalArgumentException"
        );

        assertEquals(
                "Training with id already exists: 1",
                exception.getMessage(),
                "Exception message should contain duplicate training id"
        );
    }

    @Test
    void shouldFindTrainingByIdWhenTrainingExists() {
        var training = createTraining(1L);
        storage.put(1L, training);

        var result = trainingDao.findById(1L);

        assertTrue(result.isPresent(), "Training should be found by existing id");
        assertEquals(training, result.get(), "Found training should match expected training");
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainingDoesNotExist() {
        var result = trainingDao.findById(99L);

        assertTrue(result.isEmpty(), "Result should be empty when training id does not exist");
    }

    @Test
    void shouldReturnAllTrainings() {
        var firstTraining = createTraining(1L);
        var secondTraining = createTraining(2L);

        storage.put(1L, firstTraining);
        storage.put(2L, secondTraining);

        var result = trainingDao.findAll();

        assertEquals(2, result.size(), "Result should contain two trainings");
        assertTrue(result.contains(firstTraining), "Result should contain first training");
        assertTrue(result.contains(secondTraining), "Result should contain second training");
    }

    @Test
    void shouldReturnUnmodifiableListWhenFindAll() {
        storage.put(1L, createTraining(1L));

        var result = trainingDao.findAll();

        assertThrows(
                UnsupportedOperationException.class,
                () -> result.add(createTraining(2L)),
                "Returned training list should be unmodifiable"
        );
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
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();
    }
}
