package com.javaproject.iriscalendar.util;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTime {
    private String date;
    private DifferentTime availableTime;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
}
