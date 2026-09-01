package com.example.trainer_workload_service.service;

import com.example.trainer_workload_service.converter.MonthlyWorkloadToResponseDtoConverter;
import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;
import com.example.trainer_workload_service.exception.TrainerWorkloadNotFoundException;
import com.example.trainer_workload_service.model.ActionType;
import com.example.trainer_workload_service.model.MonthlySummary;
import com.example.trainer_workload_service.model.TrainerWorkload;
import com.example.trainer_workload_service.model.YearSummary;
import com.example.trainer_workload_service.repository.TrainerWorkloadRepository;
import com.example.trainer_workload_service.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Mock
    private MonthlyWorkloadToResponseDtoConverter monthlyWorkloadToResponseDtoConverter;

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    @Test
    void updateWorkloadShouldCreateTrainerAndMonthlyWorkloadWhenTheyDoNotExist() {
        var request = createRequest(ActionType.ADD, 90);

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.empty());

        trainerWorkloadService.updateWorkload(request);

        verify(trainerWorkloadRepository)
                .findByUsername("Mike.Johnson");

        ArgumentCaptor<TrainerWorkload> workloadCaptor =
                ArgumentCaptor.forClass(TrainerWorkload.class);

        verify(trainerWorkloadRepository)
                .save(workloadCaptor.capture());

        var savedWorkload = workloadCaptor.getValue();

        assertEquals("Mike.Johnson", savedWorkload.getUsername());
        assertEquals("Mike", savedWorkload.getFirstName());
        assertEquals("Johnson", savedWorkload.getLastName());
        assertTrue(savedWorkload.getActive());

        assertEquals(1, savedWorkload.getYears().size());

        var yearSummary = savedWorkload.getYears().getFirst();

        assertEquals(2026, yearSummary.getYear());
        assertEquals(1, yearSummary.getMonths().size());

        var monthlySummary = yearSummary.getMonths().getFirst();

        assertEquals(8, monthlySummary.getMonth());
        assertEquals(90, monthlySummary.getTrainingSummaryDuration());
    }

    @Test
    void updateWorkloadShouldAddDurationToExistingMonthlyWorkload() {
        var request = createRequest(ActionType.ADD, 90);

        var trainer = createTrainer();

        var monthlySummary = MonthlySummary.builder()
                .month(8)
                .trainingSummaryDuration(60)
                .build();

        var yearSummary = YearSummary.builder()
                .year(2026)
                .build();

        yearSummary.getMonths().add(monthlySummary);
        trainer.getYears().add(yearSummary);

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        trainerWorkloadService.updateWorkload(request);

        assertEquals(
                150,
                monthlySummary.getTrainingSummaryDuration()
        );

        verify(trainerWorkloadRepository)
                .save(trainer);
    }

    @Test
    void updateWorkloadShouldSubtractDurationWhenActionIsDelete() {
        var request = createRequest(ActionType.DELETE, 90);

        var trainer = createTrainer();

        var monthlySummary = MonthlySummary.builder()
                .month(8)
                .trainingSummaryDuration(150)
                .build();

        var yearSummary = YearSummary.builder()
                .year(2026)
                .build();

        yearSummary.getMonths().add(monthlySummary);
        trainer.getYears().add(yearSummary);

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        trainerWorkloadService.updateWorkload(request);

        assertEquals(
                60,
                monthlySummary.getTrainingSummaryDuration()
        );

        verify(trainerWorkloadRepository)
                .save(trainer);
    }

    @Test
    void getWorkloadShouldReturnMonthlyWorkload() {
        var trainer = createTrainer();

        var monthlySummary = MonthlySummary.builder()
                .month(8)
                .trainingSummaryDuration(90)
                .build();

        var yearSummary = YearSummary.builder()
                .year(2026)
                .build();

        yearSummary.getMonths().add(monthlySummary);
        trainer.getYears().add(yearSummary);

        var response = TrainerWorkloadResponseDto.builder()
                .trainerUsername("Mike.Johnson")
                .trainerFirstName("Mike")
                .trainerLastName("Johnson")
                .active(true)
                .year(2026)
                .month(8)
                .trainingSummaryDuration(90)
                .build();

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        when(monthlyWorkloadToResponseDtoConverter.convert(
                trainer,
                yearSummary,
                monthlySummary
        )).thenReturn(response);

        var result = trainerWorkloadService.getWorkload(
                "Mike.Johnson",
                2026,
                8
        );

        assertSame(response, result);

        assertEquals(
                90,
                result.getTrainingSummaryDuration()
        );

        verify(trainerWorkloadRepository)
                .findByUsername("Mike.Johnson");

        verify(monthlyWorkloadToResponseDtoConverter)
                .convert(
                        trainer,
                        yearSummary,
                        monthlySummary
                );
    }

    @Test
    void getWorkloadShouldThrowExceptionWhenTrainerDoesNotExist() {
        when(trainerWorkloadRepository.findByUsername("Unknown.Trainer"))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.getWorkload(
                        "Unknown.Trainer",
                        2026,
                        8
                )
        );

        assertEquals(
                "Trainer workload not found for username: Unknown.Trainer",
                exception.getMessage()
        );

        verify(monthlyWorkloadToResponseDtoConverter, never())
                .convert(any(), any(), any());
    }

    @Test
    void getWorkloadShouldThrowExceptionWhenMonthlyWorkloadDoesNotExist() {
        var trainer = createTrainer();

        var yearSummary = YearSummary.builder()
                .year(2026)
                .build();

        trainer.getYears().add(yearSummary);

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        var exception = assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.getWorkload(
                        "Mike.Johnson",
                        2026,
                        8
                )
        );

        assertEquals(
                "Month workload not found for username: Mike.Johnson, year 2026, month 8",
                exception.getMessage()
        );

        verify(monthlyWorkloadToResponseDtoConverter, never())
                .convert(any(), any(), any());
    }

    @Test
    void updateWorkloadShouldCreateNewYearWhenTrainerAlreadyExists() {
        var request = TrainerWorkloadRequestDto.builder()
                .trainerUsername("Mike.Johnson")
                .trainerFirstName("Mike")
                .trainerLastName("Johnson")
                .active(true)
                .trainingDate(LocalDate.of(2027, 10, 20))
                .trainingDuration(90)
                .actionType(ActionType.ADD)
                .build();

        var trainer = createTrainer();

        var existingYearSummary = YearSummary.builder()
                .year(2026)
                .build();

        trainer.getYears().add(existingYearSummary);

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        trainerWorkloadService.updateWorkload(request);

        assertEquals(2, trainer.getYears().size());

        var newYearSummary = trainer.getYears().stream()
                .filter(yearSummary -> yearSummary.getYear().equals(2027))
                .findFirst()
                .orElseThrow();

        assertEquals(1, newYearSummary.getMonths().size());

        var monthlySummary = newYearSummary.getMonths().getFirst();

        assertEquals(10, monthlySummary.getMonth());
        assertEquals(90, monthlySummary.getTrainingSummaryDuration());

        verify(trainerWorkloadRepository)
                .save(trainer);
    }

    @Test
    void updateWorkloadShouldCreateNewMonthWhenYearAlreadyExists() {
        var request = TrainerWorkloadRequestDto.builder()
                .trainerUsername("Mike.Johnson")
                .trainerFirstName("Mike")
                .trainerLastName("Johnson")
                .active(true)
                .trainingDate(LocalDate.of(2026, 9, 20))
                .trainingDuration(90)
                .actionType(ActionType.ADD)
                .build();

        var trainer = createTrainer();

        var existingMonthlySummary = MonthlySummary.builder()
                .month(8)
                .trainingSummaryDuration(60)
                .build();

        var yearSummary = YearSummary.builder()
                .year(2026)
                .build();

        yearSummary.getMonths().add(existingMonthlySummary);
        trainer.getYears().add(yearSummary);

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        trainerWorkloadService.updateWorkload(request);

        assertEquals(2, yearSummary.getMonths().size());

        var newMonthlySummary = yearSummary.getMonths().stream()
                .filter(monthlySummary -> monthlySummary.getMonth().equals(9))
                .findFirst()
                .orElseThrow();

        assertEquals(90, newMonthlySummary.getTrainingSummaryDuration());

        verify(trainerWorkloadRepository)
                .save(trainer);
    }

    @Test
    void getWorkloadShouldThrowExceptionWhenYearDoesNotExist() {
        var trainer = createTrainer();

        var yearSummary = YearSummary.builder()
                .year(2025)
                .build();

        trainer.getYears().add(yearSummary);

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        var exception = assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.getWorkload(
                        "Mike.Johnson",
                        2026,
                        8
                )
        );

        assertEquals(
                "Year workload not found for trainer Mike.Johnson, year 2026",
                exception.getMessage()
        );

        verify(monthlyWorkloadToResponseDtoConverter, never())
                .convert(any(), any(), any());
    }

    private TrainerWorkloadRequestDto createRequest(
            ActionType actionType,
            int duration) {
        return TrainerWorkloadRequestDto.builder()
                .trainerUsername("Mike.Johnson")
                .trainerFirstName("Mike")
                .trainerLastName("Johnson")
                .active(true)
                .trainingDate(LocalDate.of(2026, 8, 20))
                .trainingDuration(duration)
                .actionType(actionType)
                .build();
    }

    private TrainerWorkload createTrainer() {
        return TrainerWorkload.builder()
                .id("1")
                .username("Mike.Johnson")
                .firstName("Mike")
                .lastName("Johnson")
                .active(true)
                .build();
    }
}
