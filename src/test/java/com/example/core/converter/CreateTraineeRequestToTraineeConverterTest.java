package com.example.core.converter;

import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.model.Trainee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateTraineeRequestToTraineeConverterTest {

    private final CreateTraineeRequestToTraineeConverter converter =
            new CreateTraineeRequestToTraineeConverter();

    @Test
    void shouldConvertCreateRequestToEntity() {
        CreateTraineeRequestDto request = new CreateTraineeRequestDto();
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("New York");

        Trainee trainee = converter.convert(request);

        assertNotNull(trainee, "Converted trainee should not be null");

        assertEquals(
                request.getDateOfBirth(),
                trainee.getDateOfBirth(),
                "Date of birth should be converted correctly"
        );

        assertEquals(
                request.getAddress(),
                trainee.getAddress(),
                "Address should be converted correctly"
        );
    }
}
