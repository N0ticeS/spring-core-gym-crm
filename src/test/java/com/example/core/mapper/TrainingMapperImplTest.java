package com.example.core.mapper;

import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.mapper.impl.TrainingMapperImpl;
import com.example.core.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainingMapperImplTest {

    private final TrainingMapperImpl trainingMapper = new TrainingMapperImpl();

    @Test
    void shouldMapCreateRequestToEntity() {
        CreateTrainingRequestDto request = new CreateTrainingRequestDto();
        request.setTrainingName("Morning Fitness");
        request.setTrainingDate(LocalDate.of(2026, 7, 10));
        request.setTrainingDuration(60);

        Training training = trainingMapper.toEntity(request);

        assertNotNull(training, "Mapped training should not be null");

        assertEquals(
                request.getTrainingName(),
                training.getTrainingName(),
                "Training name should be mapped correctly"
        );

        assertEquals(
                request.getTrainingDate(),
                training.getTrainingDate(),
                "Training date should be mapped correctly"
        );

        assertEquals(
                request.getTrainingDuration(),
                training.getTrainingDuration(),
                "Training duration should be mapped correctly"
        );
    }

    @Test
    void shouldMapEntityToResponseDto() {
        User traineeUser = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .build();

        User trainerUser = User.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .username("Mike.Johnson")
                .build();

        Trainee trainee = Trainee.builder()
                .user(traineeUser)
                .build();

        Trainer trainer = Trainer.builder()
                .user(trainerUser)
                .build();

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName("Fitness")
                .build();

        Training training = Training.builder()
                .trainingName("Morning Fitness")
                .trainingDate(LocalDate.of(2026, 7, 10))
                .trainingDuration(60)
                .trainingType(trainingType)
                .trainee(trainee)
                .trainer(trainer)
                .build();

        TrainingResponseDto response = trainingMapper.toResponseDto(training);

        assertNotNull(response, "Mapped response should not be null");

        assertEquals(
                "Morning Fitness",
                response.getTrainingName(),
                "Training name should be mapped correctly"
        );

        assertEquals(
                LocalDate.of(2026, 7, 10),
                response.getTrainingDate(),
                "Training date should be mapped correctly"
        );

        assertEquals(
                60,
                response.getTrainingDuration(),
                "Training duration should be mapped correctly"
        );

        assertEquals(
                "Fitness",
                response.getTrainingType(),
                "Training type should be mapped correctly"
        );

        assertEquals(
                "John.Smith",
                response.getTraineeUsername(),
                "Trainee username should be mapped correctly"
        );

        assertEquals(
                "Mike.Johnson",
                response.getTrainerUsername(),
                "Trainer username should be mapped correctly"
        );

        assertEquals(
                "Mike Johnson",
                response.getTrainerName(),
                "Trainer full name should be mapped correctly"
        );
    }
}