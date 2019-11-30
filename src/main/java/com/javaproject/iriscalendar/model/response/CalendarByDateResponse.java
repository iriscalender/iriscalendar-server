package com.javaproject.iriscalendar.model.response;

import com.javaproject.iriscalendar.model.entity.AutoToManualCalendar;
import com.javaproject.iriscalendar.model.entity.AutomaticCalendar;
import com.javaproject.iriscalendar.model.entity.ManualCalendar;
import com.javaproject.iriscalendar.model.request.ManualCalendarModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CalendarByDateResponse {
    @Embedded
    private List<ManualCalendar> manual;
    @Embedded
    private List<ManualCalendarModel> auto;

    public CalendarByDateResponse(List<ManualCalendar> manual, List<AutoToManualCalendar> auto) {
        ArrayList<ManualCalendarModel> newAutoList = new ArrayList<>();
        for (AutoToManualCalendar autoToManualCalendar : auto) {
            newAutoList.add(ManualCalendarModel.builder()
                    .id(autoToManualCalendar.getIdx())
                    .category(autoToManualCalendar.getAuto().getCategory())
                    .calendarName(autoToManualCalendar.getAuto().getCalendarName())
                    .startTime(autoToManualCalendar.getStartTime())
                    .endTime(autoToManualCalendar.getEndTime())
                    .build());
        }

        this.manual = manual;
        this.auto = newAutoList;
    }
}
