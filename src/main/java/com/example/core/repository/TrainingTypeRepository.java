package com.example.core.repository;

import com.example.core.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);
}
