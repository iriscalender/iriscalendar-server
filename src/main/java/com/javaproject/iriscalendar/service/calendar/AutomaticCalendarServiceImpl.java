package com.javaproject.iriscalendar.service.calendar;

import com.javaproject.iriscalendar.model.entity.AutoToManualCalendar;
import com.javaproject.iriscalendar.model.entity.AutomaticCalendar;
import com.javaproject.iriscalendar.repository.AutoToManualRepository;
import com.javaproject.iriscalendar.repository.AutomaticCalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AutomaticCalendarServiceImpl implements AutomaticCalendarService {
    @Autowired
    AutomaticCalendarRepository automaticCalendarRepository;
    @Autowired
    AutoToManualRepository autoToManualRepository;

    @Override
    public Optional<AutomaticCalendar> getAutomaticCalendarByUserId(String id) {
        return automaticCalendarRepository.findAutomaticCalendarByUserId(id);
    }

    @Override
    public AutomaticCalendar save(AutomaticCalendar calendar) {
        return automaticCalendarRepository.save(calendar);
    }

    @Override
    public Optional<ArrayList<AutomaticCalendar>> getAllByUserId(String id) {
        return automaticCalendarRepository.findAllByUserIdOrderByEndTimeAscPriorityAsc(id);
    }

    @Override
    public Optional<AutomaticCalendar> getAutomaticCalendarByIdx(Long idx) {
        return automaticCalendarRepository.findAutomaticCalendarByIdx(idx);
    }

    @Override
    public AutomaticCalendar update(AutomaticCalendar calendar, AutomaticCalendar update) {
        calendar.setCategory(update.getCategory());
        calendar.setCalendarName(update.getCalendarName());
        calendar.setEndTime(update.getEndTime());
        calendar.setRequiredTime(update.getRequiredTime());
        calendar.setParticularImportant(update.isParticularImportant());

        return this.save(calendar);
    }

    @Override
    public void delete(AutomaticCalendar calendar) {
        automaticCalendarRepository.delete(calendar);
    }

    @Override
    public Optional<AutomaticCalendar> getAutomaticCalendarByUserIdAndIdx(String id, Long idx) {
        return automaticCalendarRepository.findAutomaticCalendarByUserIdAndIdx(id, idx);
    }
}
