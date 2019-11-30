package com.javaproject.iriscalendar.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter @Setter
public class AutomaticCalendarModel {

    @NotNull
    private String category;

    @NotNull
    private String calendarName;

    @NotNull
    private String endTime;

    @NotNull
    private int requiredTime;

    @NotNull
    private boolean isParticularImportant;
}
