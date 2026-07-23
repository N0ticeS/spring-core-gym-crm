package com.example.core.service;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import com.example.core.model.TrainingType;
import com.example.core.model.User;
import com.example.core.repository.TrainerRepository;
import com.example.core.repository.TrainingRepository;
import com.example.core.repository.TrainingTypeRepository;
import com.example.core.service.impl.TrainerServiceImpl;
import com.example.core.service.util.UsernameGenerator;
import com.example.core.specification.TrainingSearchCriteria;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UsernameGenerator usernameGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void shouldCreateTrainerSuccessfully() {
        CreateTrainerRequestDto request = createTrainerRequest();

        TrainingType specialization = createTrainingType("Fitness");

        when(trainingTypeRepository.findByTrainingTypeName("Fitness"))
                .thenReturn(Optional.of(specialization));
        when(usernameGenerator.generate("Mike", "Brown")).thenReturn("Mike.Brown");
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = trainerService.create(request);

        assertEquals("Mike.Brown", result.getUsername(), "Username should match generated username");

        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void shouldThrowExceptionWhenTrainingTypeNotFoundOnCreate() {
        CreateTrainerRequestDto request = createTrainerRequest();

        when(trainingTypeRepository.findByTrainingTypeName("Fitness"))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.create(request)
        );

        assertEquals("Training type not found", exception.getMessage(),
                "Exception message should match");

        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void shouldFindTrainerByUsernameSuccessfully() {
        Trainer trainer = createTrainer(
                createUser("Mike", "Brown", "Mike.Brown", "password123"),
                createTrainingType("Fitness")
        );

        when(trainerRepository.findByUserUsername("Mike.Brown"))
                .thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findByUsername("Mike.Brown");

        assertEquals("Mike.Brown", result.getUser().getUsername(), "Trainer username should match");

        verify(trainerRepository).findByUserUsername("Mike.Brown");
    }

    @Test
    void shouldThrowExceptionWhenTrainerNotFoundByUsername() {
        when(trainerRepository.findByUserUsername("Unknown.Trainer"))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.findByUsername("Unknown.Trainer")
        );

        assertEquals("Trainer profile not found", exception.getMessage(),
                "Exception message should match");

        verify(trainerRepository).findByUserUsername("Unknown.Trainer");
    }

    @Test
    void shouldFindAllTrainersSuccessfully() {
        Trainer trainerOne = createTrainer(
                createUser("Mike", "Brown", "Mike.Brown", "password123"),
                createTrainingType("Fitness")
        );
        Trainer trainerTwo = createTrainer(
                createUser("Anna", "Wilson", "Anna.Wilson", "password123"),
                createTrainingType("Yoga")
        );

        when(trainerRepository.findAll()).thenReturn(List.of(trainerOne, trainerTwo));

        List<Trainer> result = trainerService.findAll();

        assertEquals(2, result.size(), "Two trainers should be returned");

        verify(trainerRepository).findAll();
    }

    @Test
    void shouldUpdateTrainerSuccessfully() {
        UpdateTrainerRequestDto request = updateTrainerRequest();

        Trainer trainer = createTrainer(
                createUser("Mike", "Brown", "Mike.Brown", "password123"),
                createTrainingType("Fitness")
        );

        when(trainerRepository.findByUserUsername("Mike.Brown")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer result = trainerService.update("Mike.Brown", request);

        assertEquals("Mike.Brown", result.getUser().getUsername(), "Updated trainer username should match");
        assertEquals("Mike", trainer.getUser().getFirstName(), "Trainer first name should be updated");
        assertEquals("Updated", trainer.getUser().getLastName(), "Trainer last name should be updated");

        verify(trainerRepository).save(trainer);
    }

    @Test
    void shouldChangeStatusSuccessfully() {
        Trainer trainer = createTrainer(
                createUser("Mike", "Brown", "Mike.Brown", "password123"),
                createTrainingType("Fitness")
        );

        when(trainerRepository.findByUserUsername("Mike.Brown")).thenReturn(Optional.of(trainer));

        trainerService.changeStatus("Mike.Brown", false);

        assertFalse(trainer.getUser().isActive(), "Trainer should be inactive");

        verify(trainerRepository).save(trainer);
    }

    @Test
    void shouldThrowExceptionWhenStatusIsAlreadySame() {
        Trainer trainer = createTrainer(
                createUser("Mike", "Brown", "Mike.Brown", "password123"),
                createTrainingType("Fitness")
        );

        when(trainerRepository.findByUserUsername("Mike.Brown")).thenReturn(Optional.of(trainer));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> trainerService.changeStatus("Mike.Brown", true)
        );

        assertEquals("Trainer already has this status", exception.getMessage(),
                "Exception message should match");

        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void shouldDeleteTrainerSuccessfully() {
        Trainer trainer = createTrainer(
                createUser("Mike", "Brown", "Mike.Brown", "password123"),
                createTrainingType("Fitness")
        );

        when(trainerRepository.findByUserUsername("Mike.Brown")).thenReturn(Optional.of(trainer));

        trainerService.deleteByUsername("Mike.Brown");

        verify(trainerRepository).delete(trainer);
    }

    @Test
    void shouldGetTrainerTrainingsSuccessfully() {
        Trainer trainer = createTrainer(
                createUser("Mike", "Brown", "Mike.Brown", "password123"),
                createTrainingType("Fitness")
        );

        Training training = new Training();

        TrainingSearchCriteria criteria = new TrainingSearchCriteria();

        when(trainerRepository.findByUserUsername("Mike.Brown")).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAll(any(Specification.class))).thenReturn(List.of(training));

        List<Training> result = trainerService.getTrainings("Mike.Brown", criteria);

        assertEquals(1, result.size(), "One training should be returned");
        assertEquals("Mike.Brown", criteria.getTrainerUsername(),
                "Criteria should be scoped by trainer username");

        verify(trainingRepository).findAll(any(Specification.class));
    }

    private CreateTrainerRequestDto createTrainerRequest() {
        CreateTrainerRequestDto request = new CreateTrainerRequestDto();
        request.setFirstName("Mike");
        request.setLastName("Brown");
        request.setSpecialization("Fitness");
        return request;
    }

    private UpdateTrainerRequestDto updateTrainerRequest() {
        UpdateTrainerRequestDto request = new UpdateTrainerRequestDto();
        request.setFirstName("Mike");
        request.setLastName("Updated");
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

    private Trainer createTrainer(User user, TrainingType specialization) {
        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }

    private TrainingType createTrainingType(String name) {
        return TrainingType.builder()
                .id(1L)
                .trainingTypeName(name)
                .build();
    }
}
