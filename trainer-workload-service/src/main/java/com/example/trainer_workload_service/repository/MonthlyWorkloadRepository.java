package com.example.trainer_workload_service.repository;

import com.example.trainer_workload_service.model.MonthlyWorkload;
import com.example.trainer_workload_service.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyWorkloadRepository extends JpaRepository<MonthlyWorkload, Long> {
    Optional<MonthlyWorkload> findByTrainerAndYearAndMonth(
            TrainerWorkload trainer,
            Integer year,
            Integer month
    );
}
