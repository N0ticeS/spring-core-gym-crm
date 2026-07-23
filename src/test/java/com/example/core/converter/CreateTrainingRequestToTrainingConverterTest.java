package com.example.core.converter;

import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.model.Training;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateTrainingRequestToTrainingConverterTest {

    private final CreateTrainingRequestToTrainingConverter converter =
            new CreateTrainingRequestToTrainingConverter();

    @Test
    void shouldConvertCreateRequestToEntity() {
        CreateTrainingRequestDto request = new CreateTrainingRequestDto();
        request.setTrainingName("Morning Fitness");
        request.setTrainingDate(LocalDate.of(2026, 7, 10));
        request.setTrainingDuration(60);

        Training training = converter.convert(request);

        assertNotNull(training, "Converted training should not be null");

        assertEquals(
                request.getTrainingName(),
                training.getTrainingName(),
                "Training name should be converted correctly"
        );

        assertEquals(
                request.getTrainingDate(),
                training.getTrainingDate(),
                "Training date should be converted correctly"
        );

        assertEquals(
                request.getTrainingDuration(),
                training.getTrainingDuration(),
                "Training duration should be converted correctly"
        );
    }
}
