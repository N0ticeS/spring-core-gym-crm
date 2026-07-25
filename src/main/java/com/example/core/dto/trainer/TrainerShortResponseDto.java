package com.example.core.dto.trainer;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({
        "username",
        "firstName",
        "lastName",
        "specialization"
})
public class TrainerShortResponseDto {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
}
