package com.example.core.mapper;

import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.mapper.impl.TrainerMapperImpl;
import com.example.core.model.Trainer;
import com.example.core.model.TrainingType;
import com.example.core.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperImplTest {

    private final TrainerMapperImpl trainerMapper = new TrainerMapperImpl();

    @Test
    void shouldMapCreateRequestToEntity() {
        CreateTrainerRequestDto request = new CreateTrainerRequestDto();
        request.setFirstName("Mike");
        request.setLastName("Brown");
        request.setSpecialization("Fitness");

        Trainer trainer = trainerMapper.toEntity(request);

        assertNotNull(trainer, "Mapped trainer should not be null");
    }

    @Test
    void shouldUpdateTrainerEntity() {
        User user = User.builder()
                .firstName("Mike")
                .lastName("Brown")
                .build();

        Trainer trainer = Trainer.builder()
                .user(user)
                .build();

        UpdateTrainerRequestDto request = new UpdateTrainerRequestDto();
        request.setFirstName("John");
        request.setLastName("Smith");
        request.setSpecialization("Yoga");

        trainerMapper.updateEntity(request, trainer);

        assertEquals(
                "John",
                trainer.getUser().getFirstName(),
                "First name should be updated"
        );

        assertEquals(
                "Smith",
                trainer.getUser().getLastName(),
                "Last name should be updated"
        );
    }

    @Test
    void shouldMapEntityToResponseDto() {
        User trainerUser = User.builder()
                .firstName("Mike")
                .lastName("Brown")
                .username("Mike.Brown")
                .isActive(true)
                .build();

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName("Fitness")
                .build();

        Trainer trainer = Trainer.builder()
                .user(trainerUser)
                .specialization(trainingType)
                .build();

        TrainerResponseDto response = trainerMapper.toResponseDto(trainer);

        assertNotNull(response, "Mapped response should not be null");

        assertEquals(
                "Mike",
                response.getFirstName(),
                "First name should be mapped correctly"
        );

        assertEquals(
                "Brown",
                response.getLastName(),
                "Last name should be mapped correctly"
        );

        assertEquals(
                "Mike.Brown",
                response.getUsername(),
                "Username should be mapped correctly"
        );

        assertTrue(
                response.getActive(),
                "Active flag should be mapped correctly"
        );

        assertEquals(
                "Fitness",
                response.getSpecialization(),
                "Specialization should be mapped correctly"
        );

//        assertEquals(
//                Set.of("John Smith"),
//                response.getTrainees(),
//                "Trainee full names should be mapped correctly"
//        );
    }
}