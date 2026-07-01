package com.example.core.dao.impl;

import com.example.core.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@RequiredArgsConstructor
@Repository
public class TrainerDaoImpl extends AbstractUserDao<Trainer> {

    @Qualifier("trainerStorage")
    private final Map<Long, Trainer> storage;

    @Override
    protected Map<Long, Trainer> getStorage() {
        return storage;
    }

    @Override
    protected String getEntityName() {
        return "Trainer";
    }
}
