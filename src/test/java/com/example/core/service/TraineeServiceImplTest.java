package com.example.core.service;

import com.example.core.converter.CreateTraineeRequestToTraineeConverter;
import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.User;
import com.example.core.repository.TraineeRepository;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.service.impl.TraineeServiceImpl;
import com.example.core.service.util.UsernameGenerator;
import com.example.core.specification.TrainingSearchCriteria;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private CreateTraineeRequestToTraineeConverter createTraineeConverter;

    @Mock
    private UsernameGenerator usernameGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void shouldCreateTraineeSuccessfully() {
        CreateTraineeRequestDto request = createTraineeRequest();

        Trainee trainee = createTrainee(null);

        when(usernameGenerator.generate("John", "Smith")).thenReturn("John.Smith");
        when(createTraineeConverter.convert(request)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        User result = traineeService.create(request);

        assertEquals("John.Smith", result.getUsername(), "Username should match generated username");
        assertEquals("John.Smith", trainee.getUser().getUsername(), "User should be set to trainee");

        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldThrowExceptionWhenCreateTraineeConversionFails() {
        CreateTraineeRequestDto request = createTraineeRequest();

        when(usernameGenerator.generate("John", "Smith")).thenReturn("John.Smith");
        when(createTraineeConverter.convert(request)).thenReturn(null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> traineeService.create(request)
        );

        assertEquals(
                "Failed to convert CreateTraineeRequestDto to Trainee",
                exception.getMessage(),
                "Exception message should match"
        );

        verify(traineeRepository, never()).save(any(Trainee.class));
    }

    @Test
    void shouldFindTraineeByUsernameSuccessfully() {
        User user = createUser("John", "Smith", "John.Smith", "password123");
        Trainee trainee = createTrainee(user);

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findByUsername("John.Smith");

        assertEquals("John.Smith", result.getUser().getUsername(), "Trainee username should match");

        verify(traineeRepository).findByUserUsername("John.Smith");
    }

    @Test
    void shouldThrowExceptionWhenTraineeNotFoundByUsername() {
        when(traineeRepository.findByUserUsername("Unknown.User")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.findByUsername("Unknown.User")
        );

        assertEquals("Trainee profile not found", exception.getMessage(),
                "Exception message should match");

        verify(traineeRepository).findByUserUsername("Unknown.User");
    }

    @Test
    void shouldFindAllTraineesSuccessfully() {
        Trainee traineeOne = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));
        Trainee traineeTwo = createTrainee(createUser("Jane", "Brown", "Jane.Brown", "password123"));

        when(traineeRepository.findAll()).thenReturn(List.of(traineeOne, traineeTwo));

        List<Trainee> result = traineeService.findAll();

        assertEquals(2, result.size(), "Two trainees should be returned");

        verify(traineeRepository).findAll();
    }

    @Test
    void shouldUpdateTraineeSuccessfully() {
        UpdateTraineeRequestDto request = updateTraineeRequest();

        Trainee trainee = createTrainee(
                createUser(
                        "John",
                        "Smith",
                        "John.Smith",
                        "password123"
                )
        );

        when(traineeRepository.findByUserUsername("John.Smith"))
                .thenReturn(Optional.of(trainee));

        when(traineeRepository.save(trainee))
                .thenReturn(trainee);

        Trainee result =
                traineeService.update("John.Smith", request);

        assertEquals(
                "John.Smith",
                result.getUser().getUsername(),
                "Updated trainee username should match"
        );

        assertEquals(
                request.getFirstName(),
                trainee.getUser().getFirstName(),
                "First name should be updated"
        );

        assertEquals(
                request.getLastName(),
                trainee.getUser().getLastName(),
                "Last name should be updated"
        );

        assertEquals(
                request.getDateOfBirth(),
                trainee.getDateOfBirth(),
                "Date of birth should be updated"
        );

        assertEquals(
                request.getAddress(),
                trainee.getAddress(),
                "Address should be updated"
        );

        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldKeepOptionalFieldsWhenUpdateValuesAreNull() {
        UpdateTraineeRequestDto request = new UpdateTraineeRequestDto();
        request.setFirstName("John");
        request.setLastName("Updated");

        Trainee trainee = createTrainee(
                createUser(
                        "John",
                        "Smith",
                        "John.Smith",
                        "password123"
                )
        );

        LocalDate oldDateOfBirth = trainee.getDateOfBirth();
        String oldAddress = trainee.getAddress();

        when(traineeRepository.findByUserUsername("John.Smith"))
                .thenReturn(Optional.of(trainee));

        when(traineeRepository.save(trainee))
                .thenReturn(trainee);

        traineeService.update("John.Smith", request);

        assertEquals(
                oldDateOfBirth,
                trainee.getDateOfBirth(),
                "Date of birth should remain unchanged"
        );

        assertEquals(
                oldAddress,
                trainee.getAddress(),
                "Address should remain unchanged"
        );

        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldChangeStatusSuccessfully() {
        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));

        traineeService.changeStatus("John.Smith", false);

        assertFalse(trainee.getUser().isActive(), "Trainee should be inactive");

        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldThrowExceptionWhenStatusIsAlreadySame() {
        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> traineeService.changeStatus("John.Smith", true)
        );

        assertEquals("Trainee already has this status", exception.getMessage(),
                "Exception message should match");

        verify(traineeRepository, never()).save(any(Trainee.class));
    }

    @Test
    void shouldDeleteTraineeSuccessfully() {
        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));

        traineeService.deleteByUsername("John.Smith");

        verify(trainingRepository).deleteByTraineeUserUsername("John.Smith");
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void shouldGetTraineeTrainingsSuccessfully() {
        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));
        Training training = new Training();

        TrainingSearchCriteria criteria = new TrainingSearchCriteria();

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAll(any(Specification.class))).thenReturn(List.of(training));

        List<Training> result = traineeService.getTrainings("John.Smith", criteria);

        assertEquals(1, result.size(), "One training should be returned");
        assertEquals("John.Smith", criteria.getTraineeUsername(),
                "Criteria should be scoped by trainee username");

        verify(trainingRepository).findAll(any(Specification.class));
    }

    @Test
    void shouldGetNotAssignedTrainersSuccessfully() {
        Trainer assignedTrainer = createTrainer(createUser("Mike", "Johnson", "Mike.Johnson", "password123"));
        Trainer notAssignedTrainer = createTrainer(createUser("Anna", "Wilson", "Anna.Wilson", "password123"));

        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));
        trainee.setTrainers(new HashSet<>(Set.of(assignedTrainer)));

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(List.of(assignedTrainer, notAssignedTrainer));

        List<Trainer> result = traineeService.getNotAssignedTrainers("John.Smith");

        assertEquals(1, result.size(), "Only one trainer should be not assigned");
        assertEquals("Anna.Wilson", result.get(0).getUser().getUsername(),
                "Not assigned trainer username should match");
    }

    @Test
    void shouldUpdateTraineeTrainersSuccessfully() {
        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));
        Trainer trainerOne = createTrainer(createUser("Mike", "Johnson", "Mike.Johnson", "password123"));
        Trainer trainerTwo = createTrainer(createUser("Anna", "Wilson", "Anna.Wilson", "password123"));

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("Mike.Johnson")).thenReturn(Optional.of(trainerOne));
        when(trainerRepository.findByUserUsername("Anna.Wilson")).thenReturn(Optional.of(trainerTwo));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTrainers(
                "John.Smith",
                Set.of("Mike.Johnson", "Anna.Wilson")
        );

        assertEquals("John.Smith", result.getUser().getUsername(), "Trainee username should match");
        assertEquals(2, trainee.getTrainers().size(), "Trainee should have two trainers");

        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldThrowExceptionWhenTrainerNotFoundDuringUpdateTrainers() {
        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));

        when(traineeRepository.findByUserUsername("John.Smith"))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findByUserUsername("Unknown.Trainer"))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.updateTrainers(
                        "John.Smith",
                        Set.of("Unknown.Trainer")
                )
        );

        assertEquals(
                "Trainer profile not found",
                exception.getMessage(),
                "Exception message should match"
        );

        verify(traineeRepository, never()).save(any(Trainee.class));
    }

    private CreateTraineeRequestDto createTraineeRequest() {
        CreateTraineeRequestDto request = new CreateTraineeRequestDto();
        request.setFirstName("John");
        request.setLastName("Smith");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("New York");
        return request;
    }

    private UpdateTraineeRequestDto updateTraineeRequest() {
        UpdateTraineeRequestDto request = new UpdateTraineeRequestDto();
        request.setFirstName("John");
        request.setLastName("Updated");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("Updated address");
        return request;
    }

    private User createUser(String firstName, String lastName, String username, String password) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(password)
                .isActive(true)
                .build();
    }

    private Trainee createTrainee(User user) {
        return Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("New York")
                .trainers(new HashSet<>())
                .build();
    }

    private Trainer createTrainer(User user) {
        return Trainer.builder()
                .user(user)
                .build();
    }
}
