package com.example.trainer_workload_service.service.impl;

import com.example.trainer_workload_service.converter.MonthlyWorkloadToResponseDtoConverter;
import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;
import com.example.trainer_workload_service.exception.TrainerWorkloadNotFoundException;
import com.example.trainer_workload_service.model.ActionType;
import com.example.trainer_workload_service.model.MonthlySummary;
import com.example.trainer_workload_service.model.TrainerWorkload;
import com.example.trainer_workload_service.model.YearSummary;
import com.example.trainer_workload_service.repository.TrainerWorkloadRepository;
import com.example.trainer_workload_service.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final MonthlyWorkloadToResponseDtoConverter monthlyWorkloadToResponseDtoConverter;

    @Override
    public void updateWorkload(TrainerWorkloadRequestDto request) {
        log.debug(
                "Updating workload, trainer {}, date {}, duration {}, action {}",
                request.getTrainerUsername(), request.getTrainingDate(),
                request.getTrainingDuration(), request.getActionType()
        );

        TrainerWorkload trainer = trainerWorkloadRepository
                .findByUsername(request.getTrainerUsername())
                .orElseGet(() -> createTrainer(request));

        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();

        var yearSummary = findOrCreateYearSummary(trainer, year);
        var monthlySummary = findOrCreateMonthlySummary(yearSummary, month);

        updateDuration(request, monthlySummary);

        trainerWorkloadRepository.save(trainer);

        log.info(
                "Workload updated successfully, trainer {}, year {}, month {}, summary duration {}, action {}",
                trainer.getUsername(), year, month, monthlySummary.getTrainingSummaryDuration(), request.getActionType()
        );
    }

    @Override
    public TrainerWorkloadResponseDto getWorkload(String username, Integer year, Integer month) {
        log.debug("Getting workload, trainer {}, year {}, month {}",
                username, year, month);

        var trainer = trainerWorkloadRepository
                .findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer workload not found, username {}", username);

                    return new TrainerWorkloadNotFoundException(
                            "Trainer workload not found for username: " + username);
                });

        var yearSummary = trainer.getYears().stream()
                .filter(summary -> summary.getYear().equals(year))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Year workload not found, trainer {}, year {}", username, year);

                    return new TrainerWorkloadNotFoundException(
                            "Year workload not found for trainer "
                                    + username + ", year " + year
                    );
                });

        var monthlySummary = yearSummary.getMonths().stream()
                .filter(summary -> summary.getMonth().equals(month))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Month workload not found, trainer {}, month {}", username, month);

                    return new TrainerWorkloadNotFoundException(
                            "Month workload not found for username: "
                                    + username + ", year " + year + ", month " + month
                    );
                });

        log.info(
                "Workload retrieved successfully, trainer {}, year {}, month {}, summary duration {}",
                username, year, month, monthlySummary.getTrainingSummaryDuration()
        );

        return monthlyWorkloadToResponseDtoConverter.convert(trainer, yearSummary, monthlySummary);
    }

    private TrainerWorkload createTrainer(TrainerWorkloadRequestDto request) {
        log.debug("Creating trainer workload profile, username {}", request.getTrainerUsername());

        return TrainerWorkload.builder()
                .username(request.getTrainerUsername())
                .firstName(request.getTrainerFirstName())
                .lastName(request.getTrainerLastName())
                .active(request.getActive())
                .build();

    }

    private YearSummary findOrCreateYearSummary(TrainerWorkload trainer, int year) {
        return trainer.getYears().stream()
                .filter(summary -> summary.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> createYearSummary(trainer, year));
    }

    private YearSummary createYearSummary(TrainerWorkload trainer, int year) {
        log.debug("Creating year summary, trainer {}, year {}", trainer.getUsername(), year);

        var yearSummary = YearSummary.builder()
                .year(year)
                .build();

        trainer.getYears().add(yearSummary);

        return yearSummary;
    }

    private MonthlySummary findOrCreateMonthlySummary(YearSummary yearSummary, int month) {
        return yearSummary.getMonths().stream()
                .filter(summary -> summary.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> createMonthlySummary(yearSummary, month));
    }

    private MonthlySummary createMonthlySummary(YearSummary yearSummary, int month) {
        log.debug("Creating monthly summary, year {}, month {}", yearSummary.getYear(), month);

        var monthlySummary = MonthlySummary.builder()
                .month(month)
                .trainingSummaryDuration(0)
                .build();

        yearSummary.getMonths().add(monthlySummary);

        return monthlySummary;
    }

    private void updateDuration(TrainerWorkloadRequestDto request, MonthlySummary monthlySummary) {
        var currentDuration = monthlySummary.getTrainingSummaryDuration();
        var trainingDuration = request.getTrainingDuration();

        if (request.getActionType() == ActionType.ADD) {
            monthlySummary.setTrainingSummaryDuration(currentDuration + trainingDuration);
        } else {
            monthlySummary.setTrainingSummaryDuration(currentDuration - trainingDuration);
        }

        log.debug("Training summary duration updated, month {}, old duration {}, new duration {}, action {}",
                monthlySummary.getMonth(), currentDuration, monthlySummary.getTrainingSummaryDuration(), request.getActionType());
    }
}
