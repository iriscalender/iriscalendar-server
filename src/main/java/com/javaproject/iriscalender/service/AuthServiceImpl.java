package com.javaproject.iriscalender.service;

import com.javaproject.iriscalender.model.entity.User;
import com.javaproject.iriscalender.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.getUserById(id);
    }
}