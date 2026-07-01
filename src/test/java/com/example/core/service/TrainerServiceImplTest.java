package com.example.core.service;

import com.example.core.dao.UserDao;
import com.example.core.model.Trainer;
import com.example.core.service.impl.TrainerServiceImpl;
import com.example.core.service.impl.UsernameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private UserDao<Trainer> trainerDao;

    @Mock
    private UsernameService usernameService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void shouldCreateTrainerSuccessfully() {
        var trainer = createTrainer(null, "John", "Smith", null);

        when(usernameService.getExistingUsernames())
                .thenReturn(Set.of("Mike.Brown"));

        var savedTrainer = createTrainer(2L, "John", "Smith", "John.Smith");
        savedTrainer.setPassword("Password12");
        savedTrainer.setActive(true);

        when(trainerDao.save(trainer)).thenReturn(savedTrainer);

        var result = trainerService.create(trainer);

        assertEquals(2L, result.getId(), "Created trainer id should be 2");
        assertEquals("John.Smith", result.getUsername(), "Created trainer username should be generated");
        assertEquals("Password12", result.getPassword(), "Created trainer password should be generated");
        assertTrue(result.isActive(), "Created trainer should be active");

        verify(usernameService).getExistingUsernames();
        verify(trainerDao).save(trainer);
    }

    @Test
    void shouldCreateTrainerWithDuplicateUsernameSuffix() {
        var trainer = createTrainer(null, "John", "Smith", null);

        when(usernameService.getExistingUsernames())
                .thenReturn(Set.of("John.Smith"));

        var savedTrainer = createTrainer(2L, "John", "Smith", "John.Smith1");
        savedTrainer.setPassword("Password12");
        savedTrainer.setActive(true);

        when(trainerDao.save(trainer)).thenReturn(savedTrainer);

        var result = trainerService.create(trainer);

        assertEquals("John.Smith1", result.getUsername(), "Created trainer username should contain duplicate suffix");

        verify(usernameService).getExistingUsernames();
        verify(trainerDao).save(trainer);
    }

    @Test
    void shouldSetUsernamePasswordAndActiveBeforeSaving() {
        var trainer = createTrainer(null, "John", "Smith", null);

        when(usernameService.getExistingUsernames())
                .thenReturn(Set.of());
        when(trainerDao.save(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = trainerService.create(trainer);

        assertEquals("John.Smith", result.getUsername(), "Trainer username should be set before saving");
        assertNotNull(result.getPassword(), "Trainer password should be set before saving");
        assertEquals(10, result.getPassword().length(), "Trainer password should have length of 10");
        assertTrue(result.isActive(), "Trainer should be active before saving");

        verify(trainerDao).save(argThat(saved ->
                "John.Smith".equals(saved.getUsername())
                        && saved.getPassword() != null
                        && saved.getPassword().length() == 10
                        && saved.isActive()
        ));
    }

    @Test
    void shouldThrowExceptionWhenCreatingNullTrainer() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainerService.create(null),
                "Creating null trainer should throw IllegalArgumentException"
        );

        assertEquals("Trainer cannot be null", exception.getMessage(), "Exception message should describe null trainer validation");

        verifyNoInteractions(trainerDao, usernameService);
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsBlank() {
        var trainer = createTrainer(null, " ", "Smith", null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainerService.create(trainer),
                "Creating trainer with blank first name should throw IllegalArgumentException"
        );

        assertEquals("First name cannot be blank", exception.getMessage(), "Exception message should describe blank first name validation");

        verifyNoInteractions(trainerDao, usernameService);
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsBlank() {
        var trainer = createTrainer(null, "John", " ", null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainerService.create(trainer),
                "Creating trainer with blank last name should throw IllegalArgumentException"
        );

        assertEquals("Last name cannot be blank", exception.getMessage(), "Exception message should describe blank last name validation");

        verifyNoInteractions(trainerDao, usernameService);
    }

    @Test
    void shouldFindTrainerById() {
        var trainer = createTrainer(1L, "John", "Smith", "John.Smith");

        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));

        var result = trainerService.findById(1L);

        assertTrue(result.isPresent(), "Trainer should be found by existing id");
        assertEquals(trainer, result.get(), "Found trainer should match expected trainer");

        verify(trainerDao).findById(1L);
    }

    @Test
    void shouldFindTrainerByUsername() {
        var trainer = createTrainer(1L, "John", "Smith", "John.Smith");

        when(trainerDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainer));

        var result = trainerService.findByUsername("John.Smith");

        assertTrue(result.isPresent(), "Trainer should be found by existing username");
        assertEquals(trainer, result.get(), "Found trainer should match expected trainer");

        verify(trainerDao).findByUsername("John.Smith");
    }

    @Test
    void shouldFindAllTrainers() {
        var trainers = List.of(
                createTrainer(1L, "John", "Smith", "John.Smith"),
                createTrainer(2L, "Mike", "Brown", "Mike.Brown")
        );

        when(trainerDao.findAll()).thenReturn(trainers);

        var result = trainerService.findAll();

        assertEquals(2, result.size(), "Result should contain two trainers");
        assertEquals(trainers, result, "Result should match expected trainer list");

        verify(trainerDao).findAll();
    }

    @Test
    void shouldUpdateTrainerSuccessfully() {
        var trainer = createTrainer(1L, "John", "Smith", "John.Smith");

        when(trainerDao.update(trainer)).thenReturn(true);

        var result = trainerService.update(trainer);

        assertTrue(result, "Update should return true for existing trainer");

        verify(trainerDao).update(trainer);
    }

    @Test
    void shouldReturnFalseWhenUpdateFails() {
        var trainer = createTrainer(99L, "John", "Smith", "John.Smith");

        when(trainerDao.update(trainer)).thenReturn(false);

        var result = trainerService.update(trainer);

        assertFalse(result, "Update should return false when DAO update fails");

        verify(trainerDao).update(trainer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNullTrainer() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainerService.update(null),
                "Updating null trainer should throw IllegalArgumentException"
        );

        assertEquals("Trainer cannot be null", exception.getMessage(), "Exception message should describe null trainer validation");

        verifyNoInteractions(trainerDao);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTrainerWithoutId() {
        var trainer = createTrainer(null, "John", "Smith", "John.Smith");

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> trainerService.update(trainer),
                "Updating trainer without id should throw IllegalArgumentException"
        );

        assertEquals("Trainer id cannot be null", exception.getMessage(), "Exception message should describe missing trainer id validation");

        verifyNoInteractions(trainerDao);
    }

    private Trainer createTrainer(
            Long id,
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
