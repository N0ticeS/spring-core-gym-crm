package com.example.core.dao;

import com.example.core.dao.impl.TraineeDaoImpl;
import com.example.core.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeDaoImplTest {

    private TraineeDaoImpl traineeDao;
    private Map<Long, Trainee> storage;

    @BeforeEach
    void setUp() {
        storage = new ConcurrentHashMap<>();

        traineeDao = new TraineeDaoImpl(storage);
    }

    @Test
    void shouldSaveTraineeWithGeneratedIdWhenIdIsNull() {
        var trainee = createTrainee(null, "John", "Smith", "John.Smith");

        var savedTrainee = traineeDao.save(trainee);

        assertNotNull(savedTrainee.getId(), "Trainee id should not be null");
        assertEquals(1L, savedTrainee.getId(), "Generated trainee id should be 1");
        assertEquals(savedTrainee, storage.get(1L), "Saved trainee should be stored by generated id");
    }

    @Test
    void shouldSaveTraineeWithExistingId() {
        var trainee = createTrainee(10L, "John", "Smith", "John.Smith");

        var savedTrainee = traineeDao.save(trainee);

        assertEquals(10L, savedTrainee.getId(), "Existing trainee id should be preserved");
        assertEquals(savedTrainee, storage.get(10L), "Saved trainee should be stored by existing id");
    }

    @Test
    void shouldGenerateNextIdBasedOnMaxExistingId() {
        storage.put(1L, createTrainee(1L, "John", "Smith", "John.Smith"));
        storage.put(5L, createTrainee(5L, "Mike", "Brown", "Mike.Brown"));

        var trainee = createTrainee(null, "Alex", "White", "Alex.White");

        var savedTrainee = traineeDao.save(trainee);

        assertEquals(6L, savedTrainee.getId(), "Generated trainee id should be one greater than max existing id");
        assertEquals(savedTrainee, storage.get(6L), "Saved trainee should be stored by generated next id");
    }

    @Test
    void shouldFindTraineeByIdWhenTraineeExists() {
        var trainee = createTrainee(1L, "John", "Smith", "John.Smith");
        storage.put(1L, trainee);

        var result = traineeDao.findById(1L);

        assertTrue(result.isPresent(), "Trainee should be found by existing id");
        assertEquals(trainee, result.get(), "Found trainee should match expected trainee");
    }

    @Test
    void shouldReturnEmptyOptionalWhenTraineeByIdDoesNotExist() {
        var result = traineeDao.findById(99L);

        assertTrue(result.isEmpty(), "Result should be empty when trainee id does not exist");
    }

    @Test
    void shouldFindAllTrainees() {
        var firstTrainee = createTrainee(1L, "John", "Smith", "John.Smith");
        var secondTrainee = createTrainee(2L, "Mike", "Brown", "Mike.Brown");

        storage.put(1L, firstTrainee);
        storage.put(2L, secondTrainee);

        var result = traineeDao.findAll();

        assertEquals(2, result.size(), "Result should contain two trainees");
        assertTrue(result.contains(firstTrainee), "Result should contain first trainee");
        assertTrue(result.contains(secondTrainee), "Result should contain second trainee");
    }

    @Test
    void shouldUpdateExistingTrainee() {
        var trainee = createTrainee(1L, "John", "Smith", "John.Smith");
        storage.put(1L, trainee);

        var updatedTrainee = createTrainee(1L, "John", "Smith", "John.Smith");
        updatedTrainee.setAddress("Los Angeles");

        var result = traineeDao.update(updatedTrainee);

        assertTrue(result, "Update should return true for existing trainee");
        assertEquals("Los Angeles", storage.get(1L).getAddress(), "Trainee address should be updated in storage");
    }

    @Test
    void shouldReturnFalseWhenUpdatingNonExistingTrainee() {
        var trainee = createTrainee(99L, "John", "Smith", "John.Smith");

        var result = traineeDao.update(trainee);

        assertFalse(result, "Update should return false for non-existing trainee");
        assertFalse(storage.containsKey(99L), "Storage should not contain non-existing trainee after failed update");
    }

    @Test
    void shouldDeleteExistingTrainee() {
        storage.put(1L, createTrainee(1L, "John", "Smith", "John.Smith"));

        var result = traineeDao.delete(1L);

        assertTrue(result, "Delete should return true for existing trainee");
        assertFalse(storage.containsKey(1L), "Storage should not contain deleted trainee");
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistingTrainee() {
        var result = traineeDao.delete(99L);

        assertFalse(result, "Delete should return false for non-existing trainee");
    }

    @Test
    void shouldFindTraineeByUsernameWhenTraineeExists() {
        var trainee = createTrainee(1L, "John", "Smith", "John.Smith");
        storage.put(1L, trainee);

        var result = traineeDao.findByUsername("John.Smith");

        assertTrue(result.isPresent(), "Trainee should be found by existing username");
        assertEquals(trainee, result.get(), "Found trainee should match expected username owner");
    }

    @Test
    void shouldReturnEmptyOptionalWhenTraineeByUsernameDoesNotExist() {
        storage.put(1L, createTrainee(1L, "John", "Smith", "John.Smith"));

        var result = traineeDao.findByUsername("Mike.Brown");

        assertTrue(result.isEmpty(), "Result should be empty when username does not exist");
    }

    private Trainee createTrainee(Long id, String firstName, String lastName, String username) {
        return Trainee.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("Password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("New York")
                .build();
    }
}
