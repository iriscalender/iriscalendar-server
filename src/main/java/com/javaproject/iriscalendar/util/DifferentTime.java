package com.javaproject.iriscalendar.util;

import com.javaproject.iriscalendar.model.entity.AutomaticCalendar;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

@Builder
@Getter @Setter
public class DifferentTime {
    private long diff;
    private long days;
    private long hours;
    private long minutes;

    public static DifferentTime differentTime(ZonedDateTime start, ZonedDateTime end) {
        long diff = start.toInstant().toEpochMilli() - end.toInstant().toEpochMilli();

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff) -
                TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff) -
                TimeUnit.DAYS.toMinutes(days) - (
                        TimeUnit.HOURS.toMinutes(hours));

        return DifferentTime.builder()
                .diff(diff)
                .days(days)
                .hours(hours)
                .minutes(minutes)
                .build();
    };
}
