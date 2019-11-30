package com.javaproject.iriscalendar.service.calendar;

import com.javaproject.iriscalendar.model.entity.ManualCalendar;
import com.javaproject.iriscalendar.model.request.ManualCalendarModel;
import com.javaproject.iriscalendar.repository.ManualCalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ManualCalendarServiceImpl implements ManualCalendarService {
    private ZoneId seoul = ZoneId.of("Asia/Seoul");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    ManualCalendarRepository manualCalendarRepository;

    private boolean isManualCalendarExistBetweenTwoTime(ZonedDateTime start, ZonedDateTime end, ManualCalendar manualCalendar) {
        ZonedDateTime manualCalendarDate = LocalDateTime.parse(manualCalendar.getStartTime(), formatter).atZone(seoul);

        long startMilliSecond = start.toInstant().toEpochMilli();
        long manualCalendarMilliSecond = manualCalendarDate.toInstant().toEpochMilli();
        long endMilliSecond = end.toInstant().toEpochMilli();

        return startMilliSecond < manualCalendarMilliSecond && manualCalendarMilliSecond < endMilliSecond;
    }

    @Override
    public Optional<ManualCalendar> getManualCalendarByUserId(String id) {
        return manualCalendarRepository.findManualCalendarByUserId(id);
    }

    @Override
    public Optional<ManualCalendar> getManualCalendarByIdx(Long idx) {
        return manualCalendarRepository.findManualCalendarByIdx(idx);
    }

    @Override
    public ManualCalendar save(ManualCalendar manualCalendar) {
        return manualCalendarRepository.save(manualCalendar);
    }

    @Override
    public ManualCalendar update(ManualCalendar calendar, ManualCalendarModel update) {
        calendar.setCategory(update.getCategory());
        calendar.setCalendarName(update.getCalendarName());
        calendar.setStartTime(update.getStartTime());
        calendar.setEndTime(update.getEndTime());

        return manualCalendarRepository.save(calendar);
    }

    @Override
    public void delete(ManualCalendar calendar) {
        manualCalendarRepository.delete(calendar);
    }

    @Override
    public Optional<ArrayList<ManualCalendar>> getAllByUserId(String id) {
        return manualCalendarRepository.findAllByUserIdOrderByStartTimeAsc(id);
    }

    @Override
    public Optional<List<ManualCalendar>> getAllByUserIdAndDate(String id, String date) {
        return manualCalendarRepository.getManualCalendarsByUserIdAndStartTimeContaining(id, date);
    }

}
