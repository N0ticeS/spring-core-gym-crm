package com.example.core.converter;

import com.example.core.dto.training.TrainingTrainerResponseDto;
import com.example.core.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainingToTrainerResponseDtoConverterTest {

    private final TrainingToTrainerResponseDtoConverter converter =
            new TrainingToTrainerResponseDtoConverter();

    @Test
    void shouldConvertEntityToResponseDto() {
        User traineeUser = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .build();

        User trainerUser = User.builder()
                .firstName("Mike")
                .lastName("Brown")
                .username("Mike.Brown")
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
                .trainingName("Morning Training")
                .trainingDate(LocalDate.of(2026, 7, 19))
                .trainingDuration(60)
                .trainingType(trainingType)
                .trainee(trainee)
                .trainer(trainer)
                .build();

        TrainingTrainerResponseDto response = converter.convert(training);

        assertNotNull(
                response,
                "Converted response should not be null"
        );

        assertEquals(
                "Morning Training",
                response.getTrainingName(),
                "Training name should be converted correctly"
        );

        assertEquals(
                LocalDate.of(2026, 7, 19),
                response.getTrainingDate(),
                "Training date should be converted correctly"
        );

        assertEquals(
                60,
                response.getTrainingDuration(),
                "Training duration should be converted correctly"
        );

        assertEquals(
                "Fitness",
                response.getTrainingType(),
                "Training type should be converted correctly"
        );

        assertEquals(
                "John.Smith",
                response.getTraineeUsername(),
                "Trainee username should be converted correctly"
        );

        assertEquals(
                "Mike.Brown",
                response.getTrainerUsername(),
                "Trainer username should be converted correctly"
        );

        assertEquals(
                "John Smith",
                response.getTraineeName(),
                "Trainee full name should be converted correctly"
        );
    }
}
