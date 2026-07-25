package com.example.core.dto.trainer;

import com.example.core.dto.trainee.TraineeShortResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponseDto {
    private String firstName;
    private String lastName;
    private String username;
    private Boolean active;
    private String specialization;
    private Set<TraineeShortResponseDto> trainees;
}
