package com.javaproject.iriscalendar.service.calendar;

import com.javaproject.iriscalendar.model.entity.AutoToManualCalendar;
import com.javaproject.iriscalendar.model.entity.AutomaticCalendar;
import com.javaproject.iriscalendar.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AutoToManualCalendarService {
    AutoToManualCalendar save(AutoToManualCalendar autoToManualCalendar);
    List<AutoToManualCalendar> saveAll(List<AutoToManualCalendar> autoToManualCalendars);
    Optional<List<AutoToManualCalendar>> getAllByUserIdAndDate(String id, String date) throws Exception;
    void addNewCalendar(AutomaticCalendar newCalendar, String userId);
    void deleteAllByAutomaticCalendar(AutomaticCalendar automaticCalendar);
    AutoToManualCalendar test(AutomaticCalendar newCalendar);
    AutoToManualCalendar testUpdateV2(User user, AutomaticCalendar automaticCalendar);
}
