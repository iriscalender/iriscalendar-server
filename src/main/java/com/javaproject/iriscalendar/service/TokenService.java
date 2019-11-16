package com.javaproject.iriscalendar.service;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    String createToken(String userID);
    String getIdentity(String auth);
}
