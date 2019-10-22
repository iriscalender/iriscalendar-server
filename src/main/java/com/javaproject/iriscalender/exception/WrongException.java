package com.javaproject.iriscalender.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class WrongException extends RuntimeException {
    public WrongException(String message) {
        super(message);
    }
}