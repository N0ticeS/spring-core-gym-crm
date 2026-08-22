package com.example.trainer_workload_service.service;

import com.example.trainer_workload_service.dto.TrainerWorkloadRequestDto;
import com.example.trainer_workload_service.model.MonthlyWorkload;

public interface TrainerWorkloadService {

    void updateWorkload(TrainerWorkloadRequestDto request);

    MonthlyWorkload getWorkload(
            String username,
            Integer year,
            Integer month
    );
}
