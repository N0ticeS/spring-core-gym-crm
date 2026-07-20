package com.example.core.controller;

import com.example.core.converter.TraineeToTraineeResponseDtoConverter;
import com.example.core.converter.TrainerToTrainerResponseDtoConverter;
import com.example.core.converter.TrainingToTrainingResponseDtoConverter;
import com.example.core.converter.UserToCreatedProfileResponseDtoConverter;
import com.example.core.dto.auth.ChangeStatusRequestDto;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.error.ErrorResponseDto;
import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.TraineeResponseDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.dto.trainee.UpdateTraineeTrainersRequestDto;
import com.example.core.dto.trainer.TrainerResponseDto;
import com.example.core.dto.training.TrainingResponseDto;
import com.example.core.service.TraineeService;
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
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@Tag(
        name = "Trainees",
        description = "Operations for managing trainee profiles"
)
public class TraineeController {

    private final TraineeService traineeService;

    private final UserToCreatedProfileResponseDtoConverter userToCreatedProfileResponseDtoConverter;
    private final TraineeToTraineeResponseDtoConverter traineeResponseConverter;
    private final TrainerToTrainerResponseDtoConverter trainerResponseConverter;
    private final TrainingToTrainingResponseDtoConverter trainingResponseConverter;

    @Operation(
            summary = "Create trainee profile",
            description = "Creates a new trainee profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PostMapping
    public ResponseEntity<CreatedProfileResponseDto> create(
            @Valid @RequestBody CreateTraineeRequestDto request) {

        var user = traineeService.create(request);

        var response = userToCreatedProfileResponseDtoConverter.convert(user);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get trainee profile",
            description = "Returns trainee profile by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile found"),
            @ApiResponse(responseCode = "404", description = "Trainee profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeResponseDto> getTraineeByUsername(@PathVariable String username) {

        var trainee = traineeService.findByUsername(username);

        var response = traineeResponseConverter.convert(trainee);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all trainee profiles",
            description = "Returns all trainee profiles"
    )
    @ApiResponse(responseCode = "200", description = "Trainee profiles retrieved successfully")
    @GetMapping
    public ResponseEntity<List<TraineeResponseDto>> getAllTrainees() {

        var response = traineeService.findAll()
                .stream()
                .map(traineeResponseConverter::convert)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update trainee profile",
            description = "Updates trainee profile by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Trainee profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PutMapping("/{username}")
    public ResponseEntity<TraineeResponseDto> updateTraineeByUsername(@PathVariable String username,
                                                                      @Valid @RequestBody UpdateTraineeRequestDto request) {
        var trainee = traineeService.update(username, request);

        var response = traineeResponseConverter.convert(trainee);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Change trainee status",
            description = "Activates or deactivates trainee profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Trainee profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    )),
            @ApiResponse(responseCode = "409", description = "Trainee already has the requested status",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable String username,
            @Valid @RequestBody ChangeStatusRequestDto request) {

        traineeService.changeStatus(username, request.getActive());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Delete trainee profile",
            description = "Deletes trainee profile by username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTraineeByUsername(@PathVariable String username) {

        traineeService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get trainee trainings",
            description = "Returns trainee trainings filtered by optional search criteria"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingResponseDto>> getTrainings(
            @PathVariable String username,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false)
            String trainerName,

            @RequestParam(required = false)
            Long trainingTypeId) {

        var criteria = TrainingSearchCriteria.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerName(trainerName)
                .trainingTypeId(trainingTypeId)
                .build();

        var response = traineeService.getTrainings(username, criteria)
                .stream()
                .map(trainingResponseConverter::convert)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get unassigned trainers",
            description = "Returns trainers that are not assigned to the specified trainee"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unassigned trainers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @GetMapping("/{username}/trainers/unassigned")
    public ResponseEntity<List<TrainerResponseDto>> getUnassignedTrainers(
            @PathVariable String username) {

        var response = traineeService.getNotAssignedTrainers(username)
                .stream()
                .map(trainerResponseConverter::convert)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update trainee trainers",
            description = "Replaces the list of trainers assigned to the specified trainee"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee trainers updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer profile not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDto.class
                            )
                    ))
    })
    @PutMapping("/{username}/trainers")
    public ResponseEntity<TraineeResponseDto> updateTrainers(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequestDto request) {

        var trainee = traineeService.updateTrainers(username, request.getTrainerUsernames());

        var response = traineeResponseConverter.convert(trainee);

        return ResponseEntity.ok(response);
    }
}
