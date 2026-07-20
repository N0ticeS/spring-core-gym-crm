package com.example.core.converter;

import com.example.core.model.TrainingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingTypeResponseConverterTest {

    private final TrainingTypeResponseConverter converter =
            new TrainingTypeResponseConverter();

    @Test
    void convertShouldReturnTrainingTypeResponseDto() {
        var trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();

        var result = converter.convert(trainingType);

        assertEquals(1L, result.getId());
        assertEquals("Fitness", result.getTrainingType());
    }
}
