package com.example.trainer_workload_service.service.impl;

import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.exception.TrainerWorkloadNotFoundException;
import com.example.trainer_workload_service.model.ActionType;
import com.example.trainer_workload_service.model.MonthlyWorkload;
import com.example.trainer_workload_service.model.TrainerWorkload;
import com.example.trainer_workload_service.repository.MonthlyWorkloadRepository;
import com.example.trainer_workload_service.repository.TrainerWorkloadRepository;
import com.example.trainer_workload_service.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final MonthlyWorkloadRepository monthlyWorkloadRepository;

    @Override
    @Transactional
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

        MonthlyWorkload monthlyWorkload = monthlyWorkloadRepository
                .findByTrainerAndYearAndMonth(trainer, year, month)
                .orElseGet(() -> createMonthlyWorkload(trainer, year, month));

        updateDuration(request, monthlyWorkload);

        monthlyWorkloadRepository.save(monthlyWorkload);

        log.info(
                "Workload updated successfully, trainer {}, year {}, month {}, summary duration {}, action {}",
                trainer.getUsername(), year, month, monthlyWorkload.getTrainingSummaryDuration(), request.getActionType()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyWorkload getWorkload(
            String username, Integer year, Integer month) {
        log.debug(
                "Getting workload, trainer {}, year {}, month {}",
                username, year, month
        );

        var trainer = trainerWorkloadRepository
                .findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer workload not found, username {}", username);

                    return new TrainerWorkloadNotFoundException(
                            "Trainer workload not found for username: " + username);
                });

        var workload = monthlyWorkloadRepository
                .findByTrainerAndYearAndMonth(trainer, year, month)
                .orElseThrow(() -> {
                    log.warn("Monthly workload not found, trainer {}, year {}, month {}",
                            username, year, month);

                    return new TrainerWorkloadNotFoundException(
                            "Monthly workload not found for trainer "
                                    + username + ", year " + year + ", month " + month);
                });

        log.info(
                "Workload retrieved successfully, trainer {}, year {}, month {}, summary duration {}",
                username, year, month, workload.getTrainingSummaryDuration()
        );

        return workload;
    }

    private TrainerWorkload createTrainer(TrainerWorkloadRequestDto request) {
        log.debug("Creating trainer workload profile, username {}", request.getTrainerUsername());

        TrainerWorkload trainer = TrainerWorkload.builder()
                .username(request.getTrainerUsername())
                .firstName(request.getTrainerFirstName())
                .lastName(request.getTrainerLastName())
                .active(request.getActive())
                .build();

        return trainerWorkloadRepository.save(trainer);
    }

    private MonthlyWorkload createMonthlyWorkload(TrainerWorkload trainer, int year, int month) {
        return MonthlyWorkload.builder()
                .trainer(trainer)
                .year(year)
                .month(month)
                .trainingSummaryDuration(0)
                .build();
    }

    private void updateDuration(TrainerWorkloadRequestDto request,
                                MonthlyWorkload monthlyWorkload) {

        int currentDuration = monthlyWorkload.getTrainingSummaryDuration();
        int trainingDuration = request.getTrainingDuration();

        if (request.getActionType() == ActionType.ADD) {
            monthlyWorkload.setTrainingSummaryDuration(
                    currentDuration + trainingDuration
            );
        } else {
            monthlyWorkload.setTrainingSummaryDuration(
                    currentDuration - trainingDuration
            );
        }
    }
}
