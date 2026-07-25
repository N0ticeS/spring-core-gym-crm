package com.example.core.dto.trainingtype;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainingTypeResponseDto {
    private Long id;
    private String trainingType;
}
