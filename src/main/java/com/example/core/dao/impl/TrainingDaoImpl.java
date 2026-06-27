package com.example.core.dao.impl;

import com.example.core.dao.TrainingDao;
import com.example.core.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    @Qualifier("trainingStorage")
    private final Map<Long, Training> storage;

    @Override
    public Training save(Training training) {
        if (training == null) {
            log.warn("Attempted to save null training");
            throw new IllegalArgumentException("Training cannot be null");
        }

        if (training.getId() == null) {
            Long generatedId = generateNextId();
            training.setId(generatedId);
            log.debug("Generated id {} for new training", generatedId);
        }

        if (storage.containsKey(training.getId())) {
            log.warn("Training with id {} already exists", training.getId());
            throw new IllegalArgumentException("Training with id already exists: " + training.getId());
        }

        storage.put(training.getId(), training);

        log.info("Training saved successfully with id {}", training.getId());

        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        if (id == null) {
            log.warn("Attempted to find training without id");
        }

        log.debug("Finding training with id {}", id);

        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Training> findAll() {
        log.debug("Finding all trainings");

        return List.copyOf(storage.values());
    }

    private Long generateNextId() {
        return storage.keySet()
                .stream()
                .max(Comparator.naturalOrder())
                .map(id -> id + 1)
                .orElse(1L);
    }
}
