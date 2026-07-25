package com.example.core.converter;

import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.model.Training;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateTrainingRequestToTrainingConverter implements Converter<CreateTrainingRequestDto, Training> {

    @Override
    public Training convert(CreateTrainingRequestDto source) {
        return Training.builder()
                .trainingName(source.getTrainingName())
                .trainingDate(source.getTrainingDate())
                .trainingDuration(source.getTrainingDuration())
                .build();
    }
}
