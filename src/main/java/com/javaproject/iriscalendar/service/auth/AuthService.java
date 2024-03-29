package com.javaproject.iriscalendar.service.auth;

import com.javaproject.iriscalendar.model.entity.User;

import java.util.Optional;


public interface AuthService {
    void saveUser(User user);

    Optional<User> getUserById(String id);
}
