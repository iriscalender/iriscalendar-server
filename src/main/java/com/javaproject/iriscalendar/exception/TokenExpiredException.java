package com.javaproject.iriscalendar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Token has expired.")
public class TokenExpiredException extends RuntimeException {
}
