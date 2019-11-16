package com.javaproject.iriscalendar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class WrongException extends RuntimeException {
    public WrongException(String message) {
        super(message);
    }
}