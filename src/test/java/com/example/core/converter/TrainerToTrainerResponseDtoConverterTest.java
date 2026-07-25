package com.example.core.converter;

import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.TrainingType;
import com.example.core.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrainerToTrainerResponseDtoConverterTest {

    private final TrainerToTrainerResponseDtoConverter converter =
            new TrainerToTrainerResponseDtoConverter();

    @Test
    void shouldConvertEntityToResponseDto() {
        User trainerUser = User.builder()
                .firstName("Mike")
                .lastName("Brown")
                .username("Mike.Brown")
                .isActive(true)
                .build();

        User traineeUser = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .build();

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName("Fitness")
                .build();

        Trainee trainee = Trainee.builder()
                .user(traineeUser)
                .build();

        Trainer trainer = Trainer.builder()
                .user(trainerUser)
                .specialization(trainingType)
                .trainees(Set.of(trainee))
                .build();

        TrainerResponseDto response = converter.convert(trainer);

        assertNotNull(
                response,
                "Converted response should not be null"
        );

        assertEquals(
                "Mike",
                response.getFirstName(),
                "First name should be converted correctly"
        );

        assertEquals(
                "Brown",
                response.getLastName(),
                "Last name should be converted correctly"
        );

        assertEquals(
                "Mike.Brown",
                response.getUsername(),
                "Username should be converted correctly"
        );

        assertTrue(
                response.getActive(),
                "Active flag should be converted correctly"
        );

        assertEquals(
                "Fitness",
                response.getSpecialization(),
                "Specialization should be converted correctly"
        );

        assertNotNull(
                response.getTrainees(),
                "Trainees should not be null"
        );

        assertEquals(
                1,
                response.getTrainees().size(),
                "Trainees count should be converted correctly"
        );

        var traineeResponse = response.getTrainees()
                .iterator()
                .next();

        assertEquals(
                "John.Smith",
                traineeResponse.getUsername(),
                "Trainee username should be converted correctly"
        );

        assertEquals(
                "John",
                traineeResponse.getFirstName(),
                "Trainee first name should be converted correctly"
        );

        assertEquals(
                "Smith",
                traineeResponse.getLastName(),
                "Trainee last name should be converted correctly"
        );
    }
}
