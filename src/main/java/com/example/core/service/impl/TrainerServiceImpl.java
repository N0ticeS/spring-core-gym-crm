package com.example.core.service.impl;

import com.example.core.dao.UserDao;
import com.example.core.model.Trainer;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl extends AbstractUserService<Trainer> {

    public TrainerServiceImpl(
            UserDao<Trainer> userDao,
            UsernameService usernameService) {
        super(userDao, usernameService);
    }

    @Override
    protected String getEntityName() {
        return "Trainer";
    }
}
