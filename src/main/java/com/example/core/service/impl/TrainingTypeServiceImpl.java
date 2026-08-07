package com.example.core.service.impl;

import com.example.core.model.TrainingType;
import com.example.core.repository.TrainingTypeRepository;
import com.example.core.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<TrainingType> findAll() {
        log.debug("Searching all training types");

        var trainingTypes = trainingTypeRepository.findAll();

        log.info("Training types found, count {}", trainingTypes.size());

        return trainingTypes;
    }
}
