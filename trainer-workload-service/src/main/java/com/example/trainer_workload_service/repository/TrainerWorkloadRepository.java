package com.example.trainer_workload_service.repository;

import com.example.trainer_workload_service.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {
    Optional<TrainerWorkload> findByUsername(String username);
}
