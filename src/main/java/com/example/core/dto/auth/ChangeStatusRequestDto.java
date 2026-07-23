package com.example.core.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusRequestDto {

    @NotNull
    private Boolean active;
}
