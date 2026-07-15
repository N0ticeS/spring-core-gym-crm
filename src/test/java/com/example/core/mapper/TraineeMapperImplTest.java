package com.example.core.mapper;

import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.mapper.impl.TraineeMapperImpl;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperImplTest {

    private final TraineeMapperImpl traineeMapper = new TraineeMapperImpl();

    @Test
    void shouldMapCreateRequestToEntity() {
        CreateTraineeRequestDto request = new CreateTraineeRequestDto();
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("New York");

        Trainee trainee = traineeMapper.toEntity(request);

        assertNotNull(trainee, "Mapped trainee should not be null");
        assertEquals(
                request.getDateOfBirth(),
                trainee.getDateOfBirth(),
                "Date of birth should be mapped correctly"
        );
        assertEquals(
                request.getAddress(),
                trainee.getAddress(),
                "Address should be mapped correctly"
        );
    }

    @Test
    void shouldUpdateTraineeEntity() {
        User user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .build();

        Trainee trainee = Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Old address")
                .build();

        UpdateTraineeRequestDto request = new UpdateTraineeRequestDto();
        request.setFirstName("Mike");
        request.setLastName("Brown");
        request.setDateOfBirth(LocalDate.of(1999, 5, 15));
        request.setAddress("New address");

        traineeMapper.updateEntity(request, trainee);

        assertEquals(
                "Mike",
                trainee.getUser().getFirstName(),
                "First name should be updated"
        );

        assertEquals(
                "Brown",
                trainee.getUser().getLastName(),
                "Last name should be updated"
        );

        assertEquals(
                LocalDate.of(1999, 5, 15),
                trainee.getDateOfBirth(),
                "Date of birth should be updated"
        );

        assertEquals(
                "New address",
                trainee.getAddress(),
                "Address should be updated"
        );
    }

    @Test
    void shouldMapEntityToResponseDto() {
        User traineeUser = User.builder()
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .isActive(true)
                .build();

        User trainerUser = User.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .build();

        Trainer trainer = Trainer.builder()
                .user(trainerUser)
                .build();

        Trainee trainee = Trainee.builder()
                .user(traineeUser)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("New York")
                .trainers(Set.of(trainer))
                .build();

        TraineeResponseDto response = traineeMapper.toResponseDto(trainee);

        assertNotNull(response, "Mapped response should not be null");

        assertEquals(
                "John",
                response.getFirstName(),
                "First name should be mapped correctly"
        );

        assertEquals(
                "Smith",
                response.getLastName(),
                "Last name should be mapped correctly"
        );

        assertEquals(
                "John.Smith",
                response.getUsername(),
                "Username should be mapped correctly"
        );

        assertTrue(
                response.getActive(),
                "Active flag should be mapped correctly"
        );

        assertEquals(
                LocalDate.of(2000, 1, 1),
                response.getDateOfBirth(),
                "Date of birth should be mapped correctly"
        );

        assertEquals(
                "New York",
                response.getAddress(),
                "Address should be mapped correctly"
        );

        assertEquals(
                Set.of("Mike Johnson"),
                response.getTrainers(),
                "Trainer full names should be mapped correctly"
        );
    }
}