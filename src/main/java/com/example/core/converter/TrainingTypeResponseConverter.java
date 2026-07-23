package com.example.core.converter;

import com.example.core.dto.trainingtype.TrainingTypeResponseDto;
import com.example.core.model.TrainingType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeResponseConverter
        implements Converter<TrainingType, TrainingTypeResponseDto> {

    @Override
    public TrainingTypeResponseDto convert(TrainingType source) {
        return TrainingTypeResponseDto.builder()
                .id(source.getId())
                .trainingType(source.getTrainingTypeName())
                .build();
    }
}
