package com.example.core.service.impl;

import com.example.core.dao.UserDao;
import com.example.core.model.Trainee;
import com.example.core.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UsernameService {
    private final UserDao<Trainee> traineeDao;
    private final UserDao<Trainer> trainerDao;

    public Set<String> getExistingUsernames() {
        return Stream.concat(
                        traineeDao.findAll().stream().map(Trainee::getUsername),
                        trainerDao.findAll().stream().map(Trainer::getUsername)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
