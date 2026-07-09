package com.example.core.specification;

import com.example.core.model.Training;
import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public final class TrainingSpecification {

    public static Specification<Training> byCriteria(TrainingSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), criteria.getFromDate()));
            }

            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), criteria.getToDate()));
            }

            if (criteria.getTrainingTypeId() != null) {
                predicates.add(cb.equal(root.get("trainingType").get("id"), criteria.getTrainingTypeId()));
            }

            if (criteria.getTrainerName() != null && !criteria.getTrainerName().isBlank()) {
                String trainerName = "%" + criteria.getTrainerName().toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("trainer").get("user").get("firstName")), trainerName),
                        cb.like(cb.lower(root.get("trainer").get("user").get("lastName")), trainerName)
                ));
            }

            if (criteria.getTraineeName() != null && !criteria.getTraineeName().isBlank()) {
                String traineeName = "%" + criteria.getTraineeName().toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("trainee").get("user").get("firstName")), traineeName),
                        cb.like(cb.lower(root.get("trainee").get("user").get("lastName")), traineeName)
                ));
            }

            if (criteria.getTraineeUsername() != null && !criteria.getTraineeUsername().isBlank()) {
                predicates.add(cb.equal(
                        root.get("trainee").get("user").get("username"),
                        criteria.getTraineeUsername()
                ));
            }

            if (criteria.getTrainerUsername() != null && !criteria.getTrainerUsername().isBlank()) {
                predicates.add(cb.equal(
                        root.get("trainer").get("user").get("username"),
                        criteria.getTrainerUsername()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Training> hasTraineeUsername(String username) {
        return (root, query, cb) ->
                cb.equal(root.get("trainee").get("user").get("username"), username);
    }

    public static Specification<Training> hasTrainerUsername(String username) {
        return (root, query, cb) ->
                cb.equal(root.get("trainer").get("user").get("username"), username);
    }
}
