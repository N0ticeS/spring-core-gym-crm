package com.example.core.service;

import com.example.core.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService<T extends User> {
    T create(T user);

    Optional<T> findById(Long id);

    Optional<T> findByUsername(String username);

    List<T> findAll();

    boolean update(T user);

    boolean delete(Long id);
}
