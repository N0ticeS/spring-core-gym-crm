package com.example.core.converter;

import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainer.TrainerShortResponseDto;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.TrainingType;
import com.example.core.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TraineeToTraineeResponseDtoConverterTest {

    private final TraineeToTraineeResponseDtoConverter converter =
            new TraineeToTraineeResponseDtoConverter();

    @Test
    void shouldConvertEntityToResponseDto() {
        User traineeUser = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .isActive(true)
                .build();

        User trainerUser = User.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .username("Mike.Johnson")
                .build();

        TrainingType specialization = TrainingType.builder()
                .trainingTypeName("Fitness")
                .build();

        Trainer trainer = Trainer.builder()
                .user(trainerUser)
                .specialization(specialization)
                .build();

        Trainee trainee = Trainee.builder()
                .user(traineeUser)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("New York")
                .trainers(Set.of(trainer))
                .build();

        TraineeResponseDto response = converter.convert(trainee);

        assertNotNull(response, "Converted response should not be null");

        assertEquals(
                "John",
                response.getFirstName(),
                "First name should be converted correctly"
        );

        assertEquals(
                "Smith",
                response.getLastName(),
                "Last name should be converted correctly"
        );

        assertEquals(
                "John.Smith",
                response.getUsername(),
                "Username should be converted correctly"
        );

        assertTrue(
                response.getActive(),
                "Active flag should be converted correctly"
        );

        assertEquals(
                LocalDate.of(2000, 1, 1),
                response.getDateOfBirth(),
                "Date of birth should be converted correctly"
        );

        assertEquals(
                "New York",
                response.getAddress(),
                "Address should be converted correctly"
        );

        TrainerShortResponseDto trainerResponse =
                response.getTrainers().iterator().next();

        assertEquals(
                1,
                response.getTrainers().size(),
                "One trainer should be converted"
        );

        assertEquals(
                "Mike.Johnson",
                trainerResponse.getUsername(),
                "Trainer username should be converted correctly"
        );

        assertEquals(
                "Mike",
                trainerResponse.getFirstName(),
                "Trainer first name should be converted correctly"
        );

        assertEquals(
                "Johnson",
                trainerResponse.getLastName(),
                "Trainer last name should be converted correctly"
        );

        assertEquals(
                "Fitness",
                trainerResponse.getSpecialization(),
                "Trainer specialization should be converted correctly"
        );
    }
}
