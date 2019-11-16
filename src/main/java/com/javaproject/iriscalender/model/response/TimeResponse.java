package com.javaproject.iriscalender.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class TimeResponse {
    private String start;
    private String end;
}