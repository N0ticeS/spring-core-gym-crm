package com.example.core.controller;

import com.example.core.converter.TrainerToTrainerResponseDtoConverter;
import com.example.core.converter.TrainingToTrainerResponseDtoConverter;
import com.example.core.converter.UserToCreatedProfileResponseDtoConverter;
import com.example.core.dto.auth.ChangeStatusRequestDto;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.error.ErrorResponseDto;
import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.dto.training.TrainingTrainerResponseDto;
import com.example.core.service.TrainerService;
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
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(
        name = "Trainers",
        description = "Operations for managing trainer profiles"
)
public class TrainerController {

    private final TrainerService trainerService;

    private final TrainerToTrainerResponseDtoConverter trainerResponseConverter;
    private final TrainingToTrainerResponseDtoConverter trainingToTrainerConverter;
    private final UserToCreatedProfileResponseDtoConverter userToCreatedProfileResponseDtoConverter;


    @Operation(
            summary = "Create trainer profile",
            description = "Creates a new trainer profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Training type not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PostMapping
    public ResponseEntity<CreatedProfileResponseDto> create(
            @Valid @RequestBody CreateTrainerRequestDto request) {

        var trainer = trainerService.create(request);

        var response = userToCreatedProfileResponseDtoConverter.convert(trainer);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get trainer profile",
            description = "Returns trainer profile by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile found"),
            @ApiResponse(responseCode = "404", description = "Trainer profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerResponseDto> findTrainerByUsername(
            @PathVariable String username) {

        var trainer = trainerService.findByUsername(username);

        var response = trainerResponseConverter.convert(trainer);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all trainer profiles",
            description = "Returns all trainer profiles"
    )
    @ApiResponse(responseCode = "200", description = "Trainer profiles retrieved successfully")
    @GetMapping
    public ResponseEntity<List<TrainerResponseDto>> findAllTrainers() {

        var response = trainerService.findAll()
                .stream()
                .map(trainerResponseConverter::convert)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update trainer profile",
            description = "Updates trainer profile by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Trainer profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PutMapping("/{username}")
    public ResponseEntity<TrainerResponseDto> updateTrainer(
            @PathVariable String username,
            @Valid @RequestBody UpdateTrainerRequestDto request) {

        var trainer = trainerService.update(username, request);

        var response = trainerResponseConverter.convert(trainer);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Change trainer status",
            description = "Activates or deactivates trainer profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Trainer profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "409", description = "Trainer already has the requested status",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> updateTrainerStatus(
            @PathVariable String username,
            @Valid @RequestBody ChangeStatusRequestDto request) {

        trainerService.changeStatus(username, request.getActive());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get trainer trainings",
            description = "Returns trainer trainings filtered by optional search criteria"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingTrainerResponseDto>> getTrainings(
            @PathVariable String username,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false)
            String traineeName) {

        var criteria = TrainingSearchCriteria.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeName(traineeName)
                .build();

        var response = trainerService.getTrainings(username, criteria)
                .stream()
                .map(trainingToTrainerConverter::convert)
                .toList();

        return ResponseEntity.ok(response);
    }
}
