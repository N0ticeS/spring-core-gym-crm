package com.example.core.service;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.exception.AuthenticationException;
import com.example.core.mapper.AuthMapper;
import com.example.core.mapper.TraineeMapper;
import com.example.core.mapper.TrainerMapper;
import com.example.core.mapper.TrainingMapper;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.User;
import com.example.core.repository.TraineeRepository;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.repository.UserRepository;
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
    private UserRepository userRepository;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private UsernameGenerator usernameGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void shouldCreateTraineeSuccessfully() {
        CreateTraineeRequestDto request = createTraineeRequest();

        Trainee trainee = createTrainee(null);

        CreatedProfileResponseDto response = CreatedProfileResponseDto.builder()
                .username("John.Smith")
                .password("password123")
                .build();

        when(usernameGenerator.generate("John", "Smith")).thenReturn("John.Smith");
        when(traineeMapper.toEntity(request)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(authMapper.toCreateProfileResponseDto(any(User.class))).thenReturn(response);

        CreatedProfileResponseDto result = traineeService.create(request);

        assertEquals("John.Smith", result.getUsername(), "Username should match generated username");
        assertEquals("John.Smith", trainee.getUser().getUsername(), "User should be set to trainee");

        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldFindTraineeByUsernameSuccessfully() {
        User user = createUser("John", "Smith", "John.Smith", "password123");
        Trainee trainee = createTrainee(user);
        TraineeResponseDto response = createTraineeResponse("John.Smith");

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toResponseDto(trainee)).thenReturn(response);

        TraineeResponseDto result = traineeService.findByUsername("John.Smith");

        assertEquals("John.Smith", result.getUsername(), "Trainee username should match");

        verify(traineeRepository).findByUserUsername("John.Smith");
        verify(traineeMapper).toResponseDto(trainee);
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
        when(traineeMapper.toResponseDto(traineeOne)).thenReturn(createTraineeResponse("John.Smith"));
        when(traineeMapper.toResponseDto(traineeTwo)).thenReturn(createTraineeResponse("Jane.Brown"));

        List<TraineeResponseDto> result = traineeService.findAll();

        assertEquals(2, result.size(), "Two trainees should be returned");

        verify(traineeRepository).findAll();
        verify(traineeMapper, times(2)).toResponseDto(any(Trainee.class));
    }

    @Test
    void shouldUpdateTraineeSuccessfully() {
        UpdateTraineeRequestDto request = updateTraineeRequest();

        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));
        TraineeResponseDto response = createTraineeResponse("John.Smith");

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeMapper.toResponseDto(trainee)).thenReturn(response);

        TraineeResponseDto result = traineeService.update("John.Smith", request);

        assertEquals("John.Smith", result.getUsername(), "Updated trainee username should match");

        verify(traineeMapper).updateEntity(request, trainee);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        ChangePasswordRequestDto request = changePasswordRequest("oldPassword", "newPassword123");

        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "oldPassword"));

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(userRepository.existsByUsernameAndPassword("John.Smith", "oldPassword")).thenReturn(true);

        traineeService.changePassword("John.Smith", request);

        assertEquals("newPassword123", trainee.getUser().getPassword(),
                "Password should be changed to new password");

        verify(traineeRepository).save(trainee);
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenOldPasswordIsInvalid() {
        ChangePasswordRequestDto request = changePasswordRequest("wrongPassword", "newPassword123");

        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "oldPassword"));

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(userRepository.existsByUsernameAndPassword("John.Smith", "wrongPassword")).thenReturn(false);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> traineeService.changePassword("John.Smith", request)
        );

        assertEquals("Invalid current password", exception.getMessage(),
                "Exception message should match");

        verify(traineeRepository, never()).save(any(Trainee.class));
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
        TrainingResponseDto response = TrainingResponseDto.builder()
                .trainingName("Morning Fitness")
                .build();

        TrainingSearchCriteria criteria = new TrainingSearchCriteria();

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAll(any(Specification.class))).thenReturn(List.of(training));
        when(trainingMapper.toResponseDto(training)).thenReturn(response);

        List<TrainingResponseDto> result = traineeService.getTrainings("John.Smith", criteria);

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

        TrainerResponseDto response = TrainerResponseDto.builder()
                .username("Anna.Wilson")
                .build();

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(List.of(assignedTrainer, notAssignedTrainer));
        when(trainerMapper.toResponseDto(notAssignedTrainer)).thenReturn(response);

        List<TrainerResponseDto> result = traineeService.getNotAssignedTrainers("John.Smith");

        assertEquals(1, result.size(), "Only one trainer should be not assigned");
        assertEquals("Anna.Wilson", result.get(0).getUsername(),
                "Not assigned trainer username should match");

        verify(trainerMapper).toResponseDto(notAssignedTrainer);
        verify(trainerMapper, never()).toResponseDto(assignedTrainer);
    }

    @Test
    void shouldUpdateTraineeTrainersSuccessfully() {
        Trainee trainee = createTrainee(createUser("John", "Smith", "John.Smith", "password123"));
        Trainer trainerOne = createTrainer(createUser("Mike", "Johnson", "Mike.Johnson", "password123"));
        Trainer trainerTwo = createTrainer(createUser("Anna", "Wilson", "Anna.Wilson", "password123"));

        TraineeResponseDto response = createTraineeResponse("John.Smith");

        when(traineeRepository.findByUserUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("Mike.Johnson")).thenReturn(Optional.of(trainerOne));
        when(trainerRepository.findByUserUsername("Anna.Wilson")).thenReturn(Optional.of(trainerTwo));
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeMapper.toResponseDto(trainee)).thenReturn(response);

        TraineeResponseDto result = traineeService.updateTrainers(
                "John.Smith",
                Set.of("Mike.Johnson", "Anna.Wilson")
        );

        assertEquals("John.Smith", result.getUsername(), "Trainee username should match");
        assertEquals(2, trainee.getTrainers().size(), "Trainee should have two trainers");

        verify(traineeRepository).save(trainee);
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

    private ChangePasswordRequestDto changePasswordRequest(String oldPassword, String newPassword) {
        ChangePasswordRequestDto request = new ChangePasswordRequestDto();
        request.setOldPassword(oldPassword);
        request.setPassword(newPassword);
        request.setConfirmPassword(newPassword);
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

    private TraineeResponseDto createTraineeResponse(String username) {
        return TraineeResponseDto.builder()
                .firstName("John")
                .lastName("Smith")
                .username(username)
                .active(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("New York")
                .trainers(Set.of())
                .build();
    }
}