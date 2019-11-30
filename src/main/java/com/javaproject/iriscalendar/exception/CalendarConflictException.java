package com.javaproject.iriscalendar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "schedule conflict")
public class CalendarConflictException extends RuntimeException {
    public CalendarConflictException(String message) {
        super(message);
    }
}