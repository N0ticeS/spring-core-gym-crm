package com.example.core.dto.trainee;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateTraineeTrainersRequestDto {

    @NotEmpty
    private Set<String> trainerUsernames;
}
