package com.javaproject.iriscalender.service;

import com.javaproject.iriscalender.model.entity.User;

import java.util.Optional;


public interface AuthService {
    void saveUser(User user);

    Optional<User> getUserById(String id);
}
