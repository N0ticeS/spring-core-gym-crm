package com.example.trainer_workload_service.service;

import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.dto.TrainerWorkloadResponseDto;

public interface TrainerWorkloadService {

    void updateWorkload(TrainerWorkloadRequestDto request);

    TrainerWorkloadResponseDto getWorkload(
            String username,
            Integer year,
            Integer month
    );
}
