package com.javaproject.iriscalendar.service.calendar;

import com.javaproject.iriscalendar.model.entity.ManualCalendar;
import com.javaproject.iriscalendar.model.request.ManualCalendarModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public interface ManualCalendarService {
    Optional<ManualCalendar> getManualCalendarByUserId(String id);
    Optional<ManualCalendar> getManualCalendarByIdx(Long idx);
    ManualCalendar save(ManualCalendar manualCalendar);
    ManualCalendar update(ManualCalendar calendar, ManualCalendarModel update);
    void delete(ManualCalendar calendar);
    Optional<ArrayList<ManualCalendar>> getAllByUserId(String id);
}
