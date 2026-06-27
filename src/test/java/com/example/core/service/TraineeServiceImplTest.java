package com.example.core.service;

import com.example.core.dao.UserDao;
import com.example.core.model.Trainee;
import com.example.core.service.impl.TraineeServiceImpl;
import com.example.core.service.impl.UsernameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private UserDao<Trainee> traineeDao;

    @Mock
    private UsernameService usernameService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void shouldCreateTraineeSuccessfully() {
        var trainee = createTrainee(null, "John", "Smith", null);

        when(usernameService.getExistingUsernames())
                .thenReturn(Set.of("Mike.Brown"));
        when(traineeDao.save(trainee))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = traineeService.create(trainee);

        assertNull(result.getId(), "Created trainee id should remain null before DAO assigns id");
        assertEquals("John.Smith", result.getUsername(), "Created trainee username should be generated");
        assertNotNull(result.getPassword(), "Created trainee password should be generated");
        assertEquals(10, result.getPassword().length(), "Created trainee password should have length of 10");
        assertTrue(result.isActive(), "Created trainee should be active");

        verify(usernameService).getExistingUsernames();
        verify(traineeDao).save(trainee);
    }

    @Test
    void shouldCreateTraineeWithDuplicateUsernameSuffix() {
        var trainee = createTrainee(null, "John", "Smith", null);

        when(usernameService.getExistingUsernames())
                .thenReturn(Set.of("John.Smith"));
        when(traineeDao.save(trainee))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = traineeService.create(trainee);

        assertEquals("John.Smith1", result.getUsername(), "Created trainee username should contain duplicate suffix");
        assertNotNull(result.getPassword(), "Created trainee password should be generated");
        assertTrue(result.isActive(), "Created trainee should be active");

        verify(usernameService).getExistingUsernames();
        verify(traineeDao).save(trainee);
    }

    @Test
    void shouldSetUsernamePasswordAndActiveBeforeSaving() {
        var trainee = createTrainee(null, "John", "Smith", null);

        when(usernameService.getExistingUsernames())
                .thenReturn(Set.of());
        when(traineeDao.save(any(Trainee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = traineeService.create(trainee);

        assertEquals("John.Smith", result.getUsername(), "Trainee username should be set before saving");
        assertNotNull(result.getPassword(), "Trainee password should be set before saving");
        assertEquals(10, result.getPassword().length(), "Trainee password should have length of 10");
        assertTrue(result.isActive(), "Trainee should be active before saving");

        verify(usernameService).getExistingUsernames();
        verify(traineeDao).save(argThat(saved ->
                "John.Smith".equals(saved.getUsername())
                        && saved.getPassword() != null
                        && saved.getPassword().length() == 10
                        && saved.isActive()
        ));
    }

    @Test
    void shouldThrowExceptionWhenCreatingNullTrainee() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.create(null),
                "Creating null trainee should throw IllegalArgumentException"
        );

        assertEquals("Trainee cannot be null", exception.getMessage(), "Exception message should describe null trainee validation");

        verifyNoInteractions(traineeDao, usernameService);
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsBlank() {
        var trainee = createTrainee(null, " ", "Smith", null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.create(trainee),
                "Creating trainee with blank first name should throw IllegalArgumentException"
        );

        assertEquals("First name cannot be blank", exception.getMessage(), "Exception message should describe blank first name validation");

        verifyNoInteractions(traineeDao, usernameService);
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsBlank() {
        var trainee = createTrainee(null, "John", " ", null);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.create(trainee),
                "Creating trainee with blank last name should throw IllegalArgumentException"
        );

        assertEquals("Last name cannot be blank", exception.getMessage(), "Exception message should describe blank last name validation");

        verifyNoInteractions(traineeDao, usernameService);
    }

    @Test
    void shouldFindTraineeById() {
        var trainee = createTrainee(1L, "John", "Smith", "John.Smith");

        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));

        var result = traineeService.findById(1L);

        assertTrue(result.isPresent(), "Trainee should be found by existing id");
        assertEquals(trainee, result.get(), "Found trainee should match expected trainee");

        verify(traineeDao).findById(1L);
    }

    @Test
    void shouldFindTraineeByUsername() {
        var trainee = createTrainee(1L, "John", "Smith", "John.Smith");

        when(traineeDao.findByUsername("John.Smith"))
                .thenReturn(Optional.of(trainee));

        var result = traineeService.findByUsername("John.Smith");

        assertTrue(result.isPresent(), "Trainee should be found by existing username");
        assertEquals(trainee, result.get(), "Found trainee should match expected trainee");

        verify(traineeDao).findByUsername("John.Smith");
    }

    @Test
    void shouldFindAllTrainees() {
        var trainees = List.of(
                createTrainee(1L, "John", "Smith", "John.Smith"),
                createTrainee(2L, "Mike", "Brown", "Mike.Brown")
        );

        when(traineeDao.findAll()).thenReturn(trainees);

        var result = traineeService.findAll();

        assertEquals(2, result.size(), "Result should contain two trainees");
        assertEquals(trainees, result, "Result should match expected trainee list");

        verify(traineeDao).findAll();
    }

    @Test
    void shouldUpdateTraineeSuccessfully() {
        var trainee = createTrainee(1L, "John", "Smith", "John.Smith");

        when(traineeDao.update(trainee)).thenReturn(true);

        var result = traineeService.update(trainee);

        assertTrue(result, "Update should return true for existing trainee");

        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldReturnFalseWhenUpdateFails() {
        var trainee = createTrainee(99L, "John", "Smith", "John.Smith");

        when(traineeDao.update(trainee)).thenReturn(false);

        var result = traineeService.update(trainee);

        assertFalse(result, "Update should return false when DAO update fails");

        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNullTrainee() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.update(null),
                "Updating null trainee should throw IllegalArgumentException"
        );

        assertEquals("Trainee cannot be null", exception.getMessage(), "Exception message should describe null trainee validation");

        verifyNoInteractions(traineeDao);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTraineeWithoutId() {
        var trainee = createTrainee(null, "John", "Smith", "John.Smith");

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> traineeService.update(trainee),
                "Updating trainee without id should throw IllegalArgumentException"
        );

        assertEquals("Trainee id cannot be null", exception.getMessage(), "Exception message should describe missing trainee id validation");

        verifyNoInteractions(traineeDao);
    }

    @Test
    void shouldDeleteTraineeSuccessfully() {
        when(traineeDao.delete(1L)).thenReturn(true);

        var result = traineeService.delete(1L);

        assertTrue(result, "Delete should return true for existing trainee");

        verify(traineeDao).delete(1L);
    }

    @Test
    void shouldReturnFalseWhenDeleteFails() {
        when(traineeDao.delete(99L)).thenReturn(false);

        var result = traineeService.delete(99L);

        assertFalse(result, "Delete should return false when DAO delete fails");

        verify(traineeDao).delete(99L);
    }

    private Trainee createTrainee(
            Long id,
            String firstName,
            String lastName,
            String username) {

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
