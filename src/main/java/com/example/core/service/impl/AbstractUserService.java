package com.example.core.service.impl;

import com.example.core.dao.UserDao;
import com.example.core.model.User;
import com.example.core.service.UserService;
import com.example.core.service.util.PasswordGenerator;
import com.example.core.service.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractUserService<T extends User> implements UserService<T> {

    private final UserDao<T> userDao;
    private final UsernameService usernameService;

    protected abstract String getEntityName();

    @Override
    public T create(T user) {
        validateForCreate(user);

        log.debug("Creating {} profile for first name {}, last name {}",
                getEntityName(), user.getFirstName(), user.getLastName());

        var username = UsernameGenerator.generate(
                user.getFirstName(),
                user.getLastName(),
                usernameService.getExistingUsernames()
        );

        var password = PasswordGenerator.generatePassword();

        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);

        var savedUser = userDao.save(user);

        log.info("{} profile created successfully, id {}, username {}",
                getEntityName(), savedUser.getId(), savedUser.getUsername());

        return savedUser;
    }

    @Override
    public Optional<T> findById(Long id) {
        log.debug("Finding {} by id {}", getEntityName(), id);

        return userDao.findById(id);
    }

    @Override
    public Optional<T> findByUsername(String username) {
        log.debug("Finding {} by username {}", getEntityName(), username);

        return userDao.findByUsername(username);
    }

    @Override
    public List<T> findAll() {
        log.debug("Finding all {}", getEntityName());

        return userDao.findAll();
    }

    @Override
    public boolean update(T user) {
        validateForUpdate(user);

        var updated = userDao.update(user);

        if (updated) {
            log.info("{} profile updated successfully, id {}", getEntityName(), user.getId());
        } else {
            log.warn("{} profile update failed, id {}", getEntityName(), user.getId());
        }

        return updated;
    }

    @Override
    public boolean delete(Long id) {
        log.debug("Deleting {} by id {}", getEntityName(), id);

        var deleted = userDao.delete(id);

        if (deleted) {
            log.info("{} profile deleted successfully, id {}", getEntityName(), id);
        } else {
            log.warn("{} profile delete failed, id {}", getEntityName(), id);
        }

        return deleted;
    }

    private void validateForCreate(T user) {
        if (user == null) {
            throw new IllegalArgumentException(getEntityName() + " cannot be null");
        }

        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
    }

    private void validateForUpdate(T user) {
        if (user == null) {
            throw new IllegalArgumentException(getEntityName() + " cannot be null");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException(getEntityName() + " id cannot be null");
        }
    }
}
