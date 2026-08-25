package com.example.trainer_workload_service.controller;

import com.example.trainer_workload_service.converter.MonthlyWorkloadToResponseDtoConverter;
import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;
import com.example.trainer_workload_service.model.MonthlyWorkload;
import com.example.trainer_workload_service.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workloads")
@RequiredArgsConstructor
public class TrainerWorkloadController {

    private final TrainerWorkloadService trainerWorkloadService;
    private final MonthlyWorkloadToResponseDtoConverter monthlyWorkloadToResponseDtoConverter;

    @Operation(
            summary = "Get trainer workload",
            description = "Returns trainer workload for the specified year and month",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainer workload retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or missing request parameters"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer workload not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerWorkloadResponseDto> getWorkload(
            @PathVariable String username,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        MonthlyWorkload monthlyWorkload = trainerWorkloadService.getWorkload(
                username, year, month
        );

        return ResponseEntity.ok(monthlyWorkloadToResponseDtoConverter.convert(monthlyWorkload));

    }
}
