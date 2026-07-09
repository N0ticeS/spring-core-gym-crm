package com.example.core.dto.trainee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeResponseDto {

    private String firstName;
    private String lastName;
    private String username;
    private Boolean active;
    private LocalDate dateOfBirth;
    private String address;
    private Set<String> trainers;
}
