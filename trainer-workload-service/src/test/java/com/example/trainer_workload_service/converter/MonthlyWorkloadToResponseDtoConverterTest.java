package com.example.trainer_workload_service.converter;

import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;
import com.example.trainer_workload_service.model.MonthlySummary;
import com.example.trainer_workload_service.model.TrainerWorkload;
import com.example.trainer_workload_service.model.YearSummary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonthlyWorkloadToResponseDtoConverterTest {

    private final MonthlyWorkloadToResponseDtoConverter converter =
            new MonthlyWorkloadToResponseDtoConverter();

    @Test
    void convertShouldConvertMonthlyWorkloadToResponseDto() {
        var trainer = TrainerWorkload.builder()
                .username("Mike.Johnson")
                .firstName("Mike")
                .lastName("Johnson")
                .active(true)
                .build();

        var yearSummary = YearSummary.builder()
                .year(2026)
                .build();

        var monthlySummary = MonthlySummary.builder()
                .month(10)
                .trainingSummaryDuration(20)
                .build();

        TrainerWorkloadResponseDto result =
                converter.convert(trainer, yearSummary, monthlySummary);

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
                        10,
                        result.getMonth()
                ),
                () -> assertEquals(
                        20,
                        result.getTrainingSummaryDuration()
                )
        );
    }
}
