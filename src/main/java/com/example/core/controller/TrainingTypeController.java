package com.example.core.controller;

import com.example.core.converter.TrainingTypeResponseConverter;
import com.example.core.dto.trainingtype.TrainingTypeResponseDto;
import com.example.core.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/training-types")
@RequiredArgsConstructor
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeResponseConverter trainingTypeResponseConverter;

    @Tag(
            name = "Training Types",
            description = "Operations for retrieving training types"
    )
    @Operation(
            summary = "Get training types",
            description = "Returns all available training types"
    )
    @ApiResponse(responseCode = "200", description = "Training types retrieved successfully")
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponseDto>> getTrainingTypes() {

        var response = trainingTypeService.findAll()
                .stream()
                .map(trainingTypeResponseConverter::convert)
                .toList();

        return ResponseEntity.ok(response);
    }
}
