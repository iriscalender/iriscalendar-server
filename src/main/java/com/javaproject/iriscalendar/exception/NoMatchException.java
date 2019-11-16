package com.javaproject.iriscalendar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT)
public class NoMatchException extends RuntimeException {
    public NoMatchException(String message) {
        super(message);
    }
}
