package com.javaproject.iriscalender.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter @Setter
public class TimeModel {
    @NotNull
    private String start;
    @NotNull
    private String end;
}
