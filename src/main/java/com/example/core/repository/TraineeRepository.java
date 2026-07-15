package com.example.core.repository;

import com.example.core.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUserUsername(String username);
    
    boolean existsByUserUsername(String username);

    void deleteByUserUsername(String username);
}
