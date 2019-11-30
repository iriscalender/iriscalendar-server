package com.javaproject.iriscalendar.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ManualCalendarModel {
    private Long id;
    @NotNull
    private String category;
    @NotNull
    private String calendarName;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
}
