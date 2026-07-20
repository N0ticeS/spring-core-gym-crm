package com.example.core.converter;

import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.model.Trainee;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateTraineeRequestToTraineeConverter implements Converter<CreateTraineeRequestDto, Trainee> {

    @Override
    public Trainee convert(CreateTraineeRequestDto source) {
        return Trainee.builder()
                .dateOfBirth(source.getDateOfBirth())
                .address(source.getAddress())
                .build();
    }
}
