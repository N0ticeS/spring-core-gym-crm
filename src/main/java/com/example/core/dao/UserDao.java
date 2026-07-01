package com.example.core.dao;

import com.example.core.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao<T extends User> {
    T save(T user);

    Optional<T> findById(Long id);

    List<T> findAll();

    Optional<T> findByUsername(String username);

    boolean update(T user);

    boolean delete(Long id);
}
