package com.example.core.storage;

import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import com.example.core.model.Training;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StorageData {
    private List<Trainee> trainees = new ArrayList<>();
    private List<Training> trainings = new ArrayList<>();
    private List<Trainer> trainers = new ArrayList<>();
}
