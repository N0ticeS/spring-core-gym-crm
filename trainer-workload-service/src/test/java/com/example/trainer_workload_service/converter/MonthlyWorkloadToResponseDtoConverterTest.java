package com.example.trainer_workload_service.converter;

import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;
import com.example.trainer_workload_service.model.MonthlyWorkload;
import com.example.trainer_workload_service.model.TrainerWorkload;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonthlyWorkloadToResponseDtoConverterTest {

    private final MonthlyWorkloadToResponseDtoConverter converter =
            new MonthlyWorkloadToResponseDtoConverter();

    @Test
    void convertShouldConvertMonthlyWorkloadToResponseDto() {
        var trainer = TrainerWorkload.builder()
                .id(1L)
                .username("Mike.Johnson")
                .firstName("Mike")
                .lastName("Johnson")
                .active(true)
                .build();

        var monthlyWorkload = MonthlyWorkload.builder()
                .id(1L)
                .trainer(trainer)
                .year(2026)
                .month(8)
                .trainingSummaryDuration(90)
                .build();

        TrainerWorkloadResponseDto result =
                converter.convert(monthlyWorkload);

        assertNotNull(result);

        assertAll(
                () -> assertEquals(
                        "Mike.Johnson",
                        result.getTrainerUsername()
                ),
                () -> assertEquals(
                        "Mike",
                        result.getTrainerFirstName()
                ),
                () -> assertEquals(
                        "Johnson",
                        result.getTrainerLastName()
                ),
                () -> assertTrue(result.getActive()),
                () -> assertEquals(
                        2026,
                        result.getYear()
                ),
                () -> assertEquals(
                        8,
                        result.getMonth()
                ),
                () -> assertEquals(
                        90,
                        result.getTrainingSummaryDuration()
                )
        );
    }
}
