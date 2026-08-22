package com.example.trainer_workload_service.service;

import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.exception.TrainerWorkloadNotFoundException;
import com.example.trainer_workload_service.model.ActionType;
import com.example.trainer_workload_service.model.MonthlyWorkload;
import com.example.trainer_workload_service.model.TrainerWorkload;
import com.example.trainer_workload_service.repository.MonthlyWorkloadRepository;
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
    private MonthlyWorkloadRepository monthlyWorkloadRepository;

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    @Test
    void updateWorkloadShouldCreateTrainerAndMonthlyWorkloadWhenTheyDoNotExist() {
        var request = createRequest(ActionType.ADD, 90);

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.empty());

        when(trainerWorkloadRepository.save(any(TrainerWorkload.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(monthlyWorkloadRepository.findByTrainerAndYearAndMonth(
                any(TrainerWorkload.class),
                eq(2026),
                eq(8)
        )).thenReturn(Optional.empty());

        trainerWorkloadService.updateWorkload(request);

        verify(trainerWorkloadRepository)
                .findByUsername("Mike.Johnson");

        verify(trainerWorkloadRepository)
                .save(any(TrainerWorkload.class));

        ArgumentCaptor<MonthlyWorkload> workloadCaptor =
                ArgumentCaptor.forClass(MonthlyWorkload.class);

        verify(monthlyWorkloadRepository)
                .save(workloadCaptor.capture());

        MonthlyWorkload savedWorkload = workloadCaptor.getValue();

        assertEquals(2026, savedWorkload.getYear());
        assertEquals(8, savedWorkload.getMonth());
        assertEquals(90, savedWorkload.getTrainingSummaryDuration());

        assertEquals(
                "Mike.Johnson",
                savedWorkload.getTrainer().getUsername()
        );
    }

    @Test
    void updateWorkloadShouldAddDurationToExistingMonthlyWorkload() {
        var request = createRequest(ActionType.ADD, 90);

        var trainer = createTrainer();

        var monthlyWorkload = MonthlyWorkload.builder()
                .trainer(trainer)
                .year(2026)
                .month(8)
                .trainingSummaryDuration(60)
                .build();

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        when(monthlyWorkloadRepository.findByTrainerAndYearAndMonth(
                trainer,
                2026,
                8
        )).thenReturn(Optional.of(monthlyWorkload));

        trainerWorkloadService.updateWorkload(request);

        assertEquals(
                150,
                monthlyWorkload.getTrainingSummaryDuration()
        );

        verify(monthlyWorkloadRepository)
                .save(monthlyWorkload);

        verify(trainerWorkloadRepository, never())
                .save(any(TrainerWorkload.class));
    }

    @Test
    void updateWorkloadShouldSubtractDurationWhenActionIsDelete() {
        var request = createRequest(ActionType.DELETE, 90);

        var trainer = createTrainer();

        var monthlyWorkload = MonthlyWorkload.builder()
                .trainer(trainer)
                .year(2026)
                .month(8)
                .trainingSummaryDuration(150)
                .build();

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        when(monthlyWorkloadRepository.findByTrainerAndYearAndMonth(
                trainer,
                2026,
                8
        )).thenReturn(Optional.of(monthlyWorkload));

        trainerWorkloadService.updateWorkload(request);

        assertEquals(
                60,
                monthlyWorkload.getTrainingSummaryDuration()
        );

        verify(monthlyWorkloadRepository)
                .save(monthlyWorkload);
    }

    @Test
    void getWorkloadShouldReturnMonthlyWorkload() {
        var trainer = createTrainer();

        var monthlyWorkload = MonthlyWorkload.builder()
                .trainer(trainer)
                .year(2026)
                .month(8)
                .trainingSummaryDuration(90)
                .build();

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        when(monthlyWorkloadRepository.findByTrainerAndYearAndMonth(
                trainer,
                2026,
                8
        )).thenReturn(Optional.of(monthlyWorkload));

        MonthlyWorkload result =
                trainerWorkloadService.getWorkload(
                        "Mike.Johnson",
                        2026,
                        8
                );

        assertSame(monthlyWorkload, result);

        assertEquals(
                90,
                result.getTrainingSummaryDuration()
        );

        verify(trainerWorkloadRepository)
                .findByUsername("Mike.Johnson");

        verify(monthlyWorkloadRepository)
                .findByTrainerAndYearAndMonth(
                        trainer,
                        2026,
                        8
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

        verify(monthlyWorkloadRepository, never())
                .findByTrainerAndYearAndMonth(
                        any(),
                        anyInt(),
                        anyInt()
                );
    }

    @Test
    void getWorkloadShouldThrowExceptionWhenMonthlyWorkloadDoesNotExist() {
        var trainer = createTrainer();

        when(trainerWorkloadRepository.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainer));

        when(monthlyWorkloadRepository.findByTrainerAndYearAndMonth(
                trainer,
                2026,
                8
        )).thenReturn(Optional.empty());

        var exception = assertThrows(
                TrainerWorkloadNotFoundException.class,
                () -> trainerWorkloadService.getWorkload(
                        "Mike.Johnson",
                        2026,
                        8
                )
        );

        assertEquals(
                "Monthly workload not found for trainer Mike.Johnson, year 2026, month 8",
                exception.getMessage()
        );

        verify(monthlyWorkloadRepository)
                .findByTrainerAndYearAndMonth(
                        trainer,
                        2026,
                        8
                );
    }

    private TrainerWorkloadRequestDto createRequest(
            ActionType actionType,
            int duration
    ) {
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
                .id(1L)
                .username("Mike.Johnson")
                .firstName("Mike")
                .lastName("Johnson")
                .active(true)
                .build();
    }
}
