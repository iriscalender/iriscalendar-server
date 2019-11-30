package com.javaproject.iriscalendar.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter @Setter
@AllArgsConstructor
public class BookedCalendar {
    private String calendar;
    private String date;
}
