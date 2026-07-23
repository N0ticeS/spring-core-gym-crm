package com.example.core.controller;

import com.example.core.converter.TrainingToTrainingResponseDtoConverter;
import com.example.core.dto.error.ErrorResponseDto;
import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.service.TrainingService;
import com.example.core.specification.TrainingSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@Tag(
        name = "Trainings",
        description = "Operations for managing trainings"
)
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingToTrainingResponseDtoConverter trainingResponseConverter;

    @Operation(
            summary = "Create training",
            description = "Creates a new training"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Trainee, trainer or training type not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PostMapping
    public ResponseEntity<Void> createTraining(
            @Valid @RequestBody CreateTrainingRequestDto request) {

        trainingService.createTraining(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get all trainings",
            description = "Returns trainings filtered by optional search criteria"
    )
    @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully")
    @GetMapping
    public ResponseEntity<List<TrainingResponseDto>> getAllTrainings(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false)
            String trainerName,

            @RequestParam(required = false)
            String traineeName,

            @RequestParam(required = false)
            Long trainingTypeId) {

        var criteria = TrainingSearchCriteria.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerName(trainerName)
                .traineeName(traineeName)
                .trainingTypeId(trainingTypeId)
                .build();

        var response = trainingService.findAll(criteria)
                .stream()
                .map(trainingResponseConverter::convert)
                .toList();

        return ResponseEntity.ok(response);
    }

}
