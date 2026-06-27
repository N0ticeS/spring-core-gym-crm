package com.example.core.dao;

import com.example.core.dao.impl.TrainerDaoImpl;
import com.example.core.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerDaoImplTest {

    private TrainerDaoImpl trainerDao;
    private Map<Long, Trainer> storage;

    @BeforeEach
    public void setup() {
        storage = new ConcurrentHashMap<>();

        trainerDao = new TrainerDaoImpl(storage);
    }

    @Test
    void shouldSaveTrainerWithGeneratedIdWhenIdIsNull() {
        var trainer = createTrainer(null, "John", "Smith", "John.Smith");

        var savedTrainer = trainerDao.save(trainer);

        assertNotNull(savedTrainer.getId(), "Trainer id should not be null");
        assertEquals(1L, savedTrainer.getId(), "Generated trainer id should be 1");
        assertEquals(savedTrainer, storage.get(1L), "Saved trainer should be stored by generated id");
    }

    @Test
    void shouldSaveTrainerWithExistingId() {
        var trainer = createTrainer(10L, "John", "Smith", "John.Smith");

        var savedTrainer = trainerDao.save(trainer);

        assertEquals(10L, savedTrainer.getId(), "Existing trainer id should be preserved");
        assertEquals(savedTrainer, storage.get(10L), "Saved trainer should be stored by existing id");
    }

    @Test
    void shouldGenerateNextIdBasedOnMaxExistingId() {
        storage.put(1L, createTrainer(1L, "John", "Smith", "John.Smith"));
        storage.put(5L, createTrainer(5L, "Mike", "Brown", "Mike.Brown"));

        var trainer = createTrainer(null, "Alex", "White", "Alex.White");

        var savedTrainer = trainerDao.save(trainer);

        assertEquals(6L, savedTrainer.getId(), "Generated trainer id should be one greater than max existing id");
        assertEquals(savedTrainer, storage.get(6L), "Saved trainer should be stored by generated next id");
    }

    @Test
    void shouldFindTrainerByIdWhenTrainerExists() {
        var trainer = createTrainer(1L, "John", "Smith", "John.Smith");
        storage.put(1L, trainer);

        var result = trainerDao.findById(1L);

        assertTrue(result.isPresent(), "Trainer should be found by existing id");
        assertEquals(trainer, result.get(), "Found trainer should match expected trainer");
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainerByIdDoesNotExist() {
        var result = trainerDao.findById(99L);

        assertTrue(result.isEmpty(), "Result should be empty when trainer id does not exist");
    }

    @Test
    void shouldFindTrainerByUsernameWhenTrainerExists() {
        var trainer = createTrainer(1L, "John", "Smith", "John.Smith");
        storage.put(1L, trainer);

        var result = trainerDao.findByUsername("John.Smith");

        assertTrue(result.isPresent(), "Trainer should be found by existing username");
        assertEquals(trainer, result.get(), "Found trainer should match expected username owner");
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainerByUsernameDoesNotExist() {
        storage.put(1L, createTrainer(1L, "John", "Smith", "John.Smith"));

        var result = trainerDao.findByUsername("Mike.Brown");

        assertTrue(result.isEmpty(), "Result should be empty when username does not exist");
    }

    @Test
    void shouldReturnAllTrainers() {
        var firstTrainer = createTrainer(1L, "John", "Smith", "John.Smith");
        var secondTrainer = createTrainer(2L, "Mike", "Brown", "Mike.Brown");

        storage.put(1L, firstTrainer);
        storage.put(2L, secondTrainer);

        var result = trainerDao.findAll();

        assertEquals(2, result.size(), "Result should contain two trainers");
        assertTrue(result.contains(firstTrainer), "Result should contain first trainer");
        assertTrue(result.contains(secondTrainer), "Result should contain second trainer");
    }

    @Test
    void shouldReturnUnmodifiableListWhenGetAll() {
        storage.put(1L, createTrainer(1L, "John", "Smith", "John.Smith"));

        var result = trainerDao.findAll();

        assertThrows(
                UnsupportedOperationException.class,
                () -> result.add(createTrainer(2L, "Mike", "Brown", "Mike.Brown")),
                "Returned trainer list should be unmodifiable"
        );
    }

    @Test
    void shouldUpdateExistingTrainer() {
        var trainer = createTrainer(1L, "John", "Smith", "John.Smith");
        storage.put(1L, trainer);

        var updatedTrainer = createTrainer(1L, "John", "Smith", "John.Smith");
        updatedTrainer.setSpecialization("CrossFit");

        var result = trainerDao.update(updatedTrainer);

        assertTrue(result, "Update should return true for existing trainer");
        assertEquals("CrossFit",
                storage.get(1L).getSpecialization(),
                "Trainer specialization should be updated in storage");
    }

    @Test
    void shouldReturnFalseWhenUpdatingNonExistingTrainer() {
        var trainer = createTrainer(99L, "John", "Smith", "John.Smith");

        var result = trainerDao.update(trainer);

        assertFalse(result, "Update should return false for non-existing trainer");
    }

    private Trainer createTrainer(Long id,
                                  String firstName,
                                  String lastName,
                                  String username) {

        return Trainer.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("Password123")
                .isActive(true)
                .specialization("Fitness")
                .build();
    }
}
