package com.example.core.repository;

import com.example.core.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingRepository extends JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {
    void deleteByTraineeUserUsername(String username);
}
