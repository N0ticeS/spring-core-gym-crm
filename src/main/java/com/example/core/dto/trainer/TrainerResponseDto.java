package com.example.core.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
