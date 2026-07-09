package com.example.core.runner;

import com.example.core.dto.auth.ChangePasswordRequestDto;
import com.example.core.dto.auth.CreatedProfileResponseDto;
import com.example.core.dto.trainee.CreateTraineeRequestDto;
import com.example.core.dto.trainee.UpdateTraineeRequestDto;
import com.example.core.dto.trainer.CreateTrainerRequestDto;
import com.example.core.dto.trainer.UpdateTrainerRequestDto;
import com.example.core.dto.training.CreateTrainingRequestDto;
import com.example.core.service.TraineeService;
import com.example.core.service.TrainerService;
import com.example.core.service.TrainingService;
import com.example.core.specification.TrainingSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Override
    public void run(String... args) {
        printTitle("GYM CRM DEMO STARTED");

        printStep("1. Create trainee");
        CreatedProfileResponseDto traineeCredentials = traineeService.create(
                new CreateTraineeRequestDto(
                        "John",
                        "Smith",
                        LocalDate.of(2000, 1, 1),
                        "New York"
                )
        );
        printResult(traineeCredentials);

        printStep("2. Create trainers");
        CreatedProfileResponseDto trainerOneCredentials = trainerService.create(
                new CreateTrainerRequestDto(
                        "Mike",
                        "Brown",
                        "Fitness"
                )
        );

        CreatedProfileResponseDto trainerTwoCredentials = trainerService.create(
                new CreateTrainerRequestDto(
                        "Anna",
                        "Wilson",
                        "Yoga"
                )
        );

        printResult(trainerOneCredentials);
        printResult(trainerTwoCredentials);

        printStep("3. Find trainee by username");
        printResult(traineeService.findByUsername(traineeCredentials.getUsername()));

        printStep("4. Find trainer by username");
        printResult(trainerService.findByUsername(trainerOneCredentials.getUsername()));

        printStep("5. Find all trainees");
        traineeService.findAll().forEach(this::printResult);

        printStep("6. Find all trainers");
        trainerService.findAll().forEach(this::printResult);

        printStep("7. Update trainee profile");
        printResult(traineeService.update(
                traineeCredentials.getUsername(),
                new UpdateTraineeRequestDto(
                        "John",
                        "Updated",
                        LocalDate.of(2000, 1, 1),
                        "Updated address"
                )
        ));

        printStep("8. Update trainer profile");
        printResult(trainerService.update(
                trainerOneCredentials.getUsername(),
                new UpdateTrainerRequestDto(
                        "Mike",
                        "Updated",
                        "Fitness"
                )
        ));

        printStep("9. Change trainee password");
        traineeService.changePassword(
                traineeCredentials.getUsername(),
                new ChangePasswordRequestDto(
                        traineeCredentials.getPassword(),
                        "newPassword123",
                        "newPassword123"
                )
        );
        printOk("Trainee password changed");

        printStep("10. Change trainer password");
        trainerService.changePassword(
                trainerOneCredentials.getUsername(),
                new ChangePasswordRequestDto(
                        trainerOneCredentials.getPassword(),
                        "newPassword456",
                        "newPassword456"
                )
        );
        printOk("Trainer password changed");

        printStep("11. Deactivate trainee");
        traineeService.changeStatus(traineeCredentials.getUsername(), false);
        printOk("Trainee deactivated");

        printStep("12. Activate trainee");
        traineeService.changeStatus(traineeCredentials.getUsername(), true);
        printOk("Trainee activated");

        printStep("13. Deactivate trainer");
        trainerService.changeStatus(trainerOneCredentials.getUsername(), false);
        printOk("Trainer deactivated");

        printStep("14. Activate trainer");
        trainerService.changeStatus(trainerOneCredentials.getUsername(), true);
        printOk("Trainer activated");

        printStep("15. Get trainers not assigned to trainee");
        traineeService.getNotAssignedTrainers(traineeCredentials.getUsername())
                .forEach(this::printResult);

        printStep("16. Update trainee trainers list");
        printResult(traineeService.updateTrainers(
                traineeCredentials.getUsername(),
                Set.of(trainerOneCredentials.getUsername(), trainerTwoCredentials.getUsername())
        ));

        printStep("17. Create training");
        printResult(trainingService.createTraining(
                new CreateTrainingRequestDto(
                        "Morning Fitness",
                        LocalDate.now().plusDays(1),
                        60,
                        traineeCredentials.getUsername(),
                        trainerOneCredentials.getUsername()
                )
        ));

        printStep("18. Find all trainings with empty criteria");
        trainingService.findAll(new TrainingSearchCriteria())
                .forEach(this::printResult);

        printStep("19. Find trainee trainings with criteria");
        TrainingSearchCriteria traineeCriteria = new TrainingSearchCriteria();
        traineeCriteria.setFromDate(LocalDate.now());
        traineeCriteria.setToDate(LocalDate.now().plusDays(7));
        traineeCriteria.setTrainerName("Mike");

        traineeService.getTrainings(traineeCredentials.getUsername(), traineeCriteria)
                .forEach(this::printResult);

        printStep("20. Find trainer trainings with criteria");
        TrainingSearchCriteria trainerCriteria = new TrainingSearchCriteria();
        trainerCriteria.setFromDate(LocalDate.now());
        trainerCriteria.setToDate(LocalDate.now().plusDays(7));
        trainerCriteria.setTraineeName("John");

        trainerService.getTrainings(trainerOneCredentials.getUsername(), trainerCriteria)
                .forEach(this::printResult);

        printStep("21. Delete trainee by username");
        traineeService.deleteByUsername(traineeCredentials.getUsername());
        printOk("Trainee deleted");

        printTitle("GYM CRM DEMO FINISHED");
    }

    private void printTitle(String title) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");
    }

    private void printStep(String step) {
        System.out.println();
        System.out.println("---------- " + step + " ----------");
    }

    private void printResult(Object object) {
        System.out.println(object);
    }

    private void printOk(String message) {
        System.out.println("[OK] " + message);
    }
}
