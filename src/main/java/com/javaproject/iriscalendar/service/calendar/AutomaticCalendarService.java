package com.javaproject.iriscalendar.service.calendar;

import com.javaproject.iriscalendar.model.entity.AutomaticCalendar;
import com.javaproject.iriscalendar.model.request.AutomaticCalendarModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public interface AutomaticCalendarService {
    public Optional<AutomaticCalendar> getAutomaticCalendarByUserIdAndIdx(String id, Long idx);
    public Optional<AutomaticCalendar> getAutomaticCalendarByUserId(String id);
    public Optional<AutomaticCalendar> getAutomaticCalendarByIdx(Long idx);
    public AutomaticCalendar save(AutomaticCalendar calendar);
    public Optional<ArrayList<AutomaticCalendar>> getAllByUserId(String id);
    public AutomaticCalendar update(AutomaticCalendar calendar, AutomaticCalendar update);
    public void delete(AutomaticCalendar calendar);
}