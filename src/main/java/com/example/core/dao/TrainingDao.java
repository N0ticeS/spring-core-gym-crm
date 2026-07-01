package com.example.core.dao;

import com.example.core.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDao {
    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();
}
