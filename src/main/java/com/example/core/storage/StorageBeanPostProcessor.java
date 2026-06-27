package com.example.core.storage;

import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageBeanPostProcessor implements BeanPostProcessor {

    private static final String TRAINEE_STORAGE_BEAN_NAME = "traineeStorage";
    private static final String TRAINER_STORAGE_BEAN_NAME = "trainerStorage";
    private static final String TRAINING_STORAGE_BEAN_NAME = "trainingStorage";

    private final StorageDataLoader storageDataLoader;

    private StorageData storageData;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof Map<?, ?> storage)) {
            return bean;
        }

        if (!isTargetStorage(beanName)) {
            return bean;
        }

        loadStorageDataIfNeeded();
        fillStorage(storage, beanName);

        return bean;
    }

    private boolean isTargetStorage(String beanName) {
        return TRAINEE_STORAGE_BEAN_NAME.equals(beanName)
                || TRAINER_STORAGE_BEAN_NAME.equals(beanName)
                || TRAINING_STORAGE_BEAN_NAME.equals(beanName);
    }

    private void loadStorageDataIfNeeded() {
        if (storageData == null) {
            storageData = storageDataLoader.load();
        }
    }

    private void fillStorage(Map<?, ?> storage, String beanName) {
        switch (beanName) {
            case TRAINEE_STORAGE_BEAN_NAME -> fillTraineeStorage(storage);
            case TRAINER_STORAGE_BEAN_NAME -> fillTrainerStorage(storage);
            case TRAINING_STORAGE_BEAN_NAME -> fillTrainingStorage(storage);
            default -> log.debug("Skipping unsupported storage bean: {}", beanName);
        }
    }

    @SuppressWarnings("unchecked")
    private void fillTraineeStorage(Map<?, ?> storage) {
        Map<Long, Trainee> traineeStorage = (Map<Long, Trainee>) storage;

        for (Trainee trainee : storageData.getTrainees()) {
            traineeStorage.put(trainee.getId(), trainee);
        }

        log.info("Trainee storage initialized with {} records", traineeStorage.size());
    }

    @SuppressWarnings("unchecked")
    private void fillTrainerStorage(Map<?, ?> storage) {
        Map<Long, Trainer> trainerStorage = (Map<Long, Trainer>) storage;

        for (Trainer trainer : storageData.getTrainers()) {
            trainerStorage.put(trainer.getId(), trainer);
        }

        log.info("Trainer storage initialized with {} records", trainerStorage.size());
    }

    @SuppressWarnings("unchecked")
    private void fillTrainingStorage(Map<?, ?> storage) {
        Map<Long, Training> trainingStorage = (Map<Long, Training>) storage;

        for (Training training : storageData.getTrainings()) {
            trainingStorage.put(training.getId(), training);
        }

        log.info("Training storage initialized with {} records", trainingStorage.size());
    }
}
