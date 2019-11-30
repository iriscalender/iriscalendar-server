package com.javaproject.iriscalendar.service.auth;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    String createToken(String userID);
    String getIdentity(String auth);
}
