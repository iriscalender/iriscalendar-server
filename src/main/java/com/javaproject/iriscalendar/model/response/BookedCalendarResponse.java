package com.javaproject.iriscalendar.model.response;

import com.javaproject.iriscalendar.util.BookedCalendar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class BookedCalendarResponse {
    @Embedded
    private List<BookedCalendar> calendar;
}
