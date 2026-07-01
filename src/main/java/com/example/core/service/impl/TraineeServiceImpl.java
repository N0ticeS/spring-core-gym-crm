package com.example.core.service.impl;

import com.example.core.dao.UserDao;
import com.example.core.model.Trainee;
import org.springframework.stereotype.Service;

@Service
public class TraineeServiceImpl extends AbstractUserService<Trainee> {
    public TraineeServiceImpl(
            UserDao<Trainee> userDao,
            UsernameService usernameService) {
        super(userDao, usernameService);
    }

    @Override
    protected String getEntityName() {
        return "Trainee";
    }
}
