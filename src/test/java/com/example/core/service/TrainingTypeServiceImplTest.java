package com.example.core.service;

import com.example.core.model.TrainingType;
import com.example.core.repository.TrainingTypeRepository;
import com.example.core.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Test
    void shouldFindAllTrainingTypesSuccessfully() {
        TrainingType fitness = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();

        TrainingType yoga = TrainingType.builder()
                .id(2L)
                .trainingTypeName("Yoga")
                .build();

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of(fitness, yoga));

        List<TrainingType> result =
                trainingTypeService.findAll();

        assertEquals(
                2,
                result.size(),
                "Two training types should be returned"
        );

        assertEquals(
                "Fitness",
                result.get(0).getTrainingTypeName(),
                "First training type should match"
        );

        assertEquals(
                "Yoga",
                result.get(1).getTrainingTypeName(),
                "Second training type should match"
        );

        verify(trainingTypeRepository).findAll();
    }
}
