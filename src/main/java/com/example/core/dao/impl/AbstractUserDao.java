package com.example.core.dao.impl;

import com.example.core.dao.UserDao;
import com.example.core.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class AbstractUserDao<T extends User> implements UserDao<T> {

    protected abstract Map<Long, T> getStorage();

    protected abstract String getEntityName();

    @Override
    public T save(T user) {
        if (user.getId() == null) {
            user.setId(generateNextId());
        }

        getStorage().put(user.getId(), user);

        log.info("{} saved successfully with id {}", getEntityName(), user.getId());

        return user;
    }

    @Override
    public Optional<T> findById(Long id) {
        log.debug("Finding {} by id {}", getEntityName(), id);

        return Optional.ofNullable(getStorage().get(id));
    }

    @Override
    public List<T> findAll() {
        log.debug("Retrieving all {} records. Count {}", getEntityName(), getStorage().size());

        return List.copyOf(getStorage().values());
    }

    @Override
    public Optional<T> findByUsername(String username) {
        log.debug("Finding {} by username {}", getEntityName(), username);

        return getStorage().values().stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }

    @Override
    public boolean update(T user) {
        var id = user.getId();

        if (!getStorage().containsKey(id)) {
            log.warn("{} with id {} not found", getEntityName(), id);
            return false;
        }

        getStorage().put(id, user);

        log.info("{} updated successfully with id {}", getEntityName(), id);

        return true;
    }

    @Override
    public boolean delete(Long id) {
        var removed = getStorage().remove(id);

        if (removed == null) {
            log.warn("{} with id {} not found for deletion", getEntityName(), id);
            return false;
        }

        log.info("{} deleted successfully with id {}", getEntityName(), id);
        return true;
    }

    private Long generateNextId() {
        return getStorage().keySet()
                .stream()
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }
}
