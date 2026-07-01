package com.example.core.dao.impl;

import com.example.core.model.Trainee;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@RequiredArgsConstructor
@Repository
public class TraineeDaoImpl extends AbstractUserDao<Trainee> {

    @Qualifier("traineeStorage")
    private final Map<Long, Trainee> storage;

    @Override
    protected Map<Long, Trainee> getStorage() {
        return storage;
    }

    @Override
    protected String getEntityName() {
        return "Trainee";
    }
}
