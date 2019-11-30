package com.javaproject.iriscalendar.service.calendar;

import com.javaproject.iriscalendar.exception.CalendarConflictException;
import com.javaproject.iriscalendar.exception.InvalidJwtAuthenticationException;
import com.javaproject.iriscalendar.model.entity.*;
import com.javaproject.iriscalendar.repository.AutoToManualRepository;
import com.javaproject.iriscalendar.repository.AutomaticCalendarRepository;
import com.javaproject.iriscalendar.repository.ManualCalendarRepository;
import com.javaproject.iriscalendar.service.time.TimeService;
import com.javaproject.iriscalendar.util.AvailableTime;
import com.javaproject.iriscalendar.util.DifferentTime;
import net.bytebuddy.dynamic.NexusAccessor;
import org.graalvm.compiler.lir.LIRInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AutoToManualCalendarServiceImpl implements AutoToManualCalendarService {
    private ZoneId seoul = ZoneId.of("Asia/Seoul");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    AutoToManualRepository autoToManualRepository;
    @Autowired
    AutomaticCalendarService automaticCalendarService;
    @Autowired
    ManualCalendarService manualCalendarService;
    @Autowired
    TimeService timeService;

    private int getIndexToInsertMostImportantCalendar(List<AutomaticCalendar> automaticCalendarList, AutomaticCalendar newCalendar) {
        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);
        ZonedDateTime newCalendarEndDate = LocalDateTime.parse(newCalendar.getEndTime(), formatter).atZone(seoul);;

        int index = 0;

        for (AutomaticCalendar existCalendar : automaticCalendarList) {

            ZonedDateTime existingCalendarEndDate = LocalDateTime.parse(existCalendar.getEndTime(), formatter).atZone(seoul);
            DifferentTime differentTime = DifferentTime.differentTime(now, existingCalendarEndDate);

            if (isNotMostImportant(existCalendar, differentTime)) {
                break;
            }
            if (isExistingCalendarMoreUrgent(newCalendarEndDate, existingCalendarEndDate)) {
                index++;
                continue;
            }

            ZonedDateTime postponedTime = existCalendarPostponed(existCalendar, newCalendar);
            if (isConflict(postponedTime)) {
                throw new CalendarConflictException("schedule conflict");
            }
        }
        return index;
    }

    private int getIndexToInsertImportantCalendar(List<AutomaticCalendar> automaticCalendarList, AutomaticCalendar newCalendar) {
        int index = 0;

        for (AutomaticCalendar existCalendar : automaticCalendarList) {

            if (isMostImportant(existCalendar, getDifferentTimeBetweenCalendarAndNow(existCalendar))) {
                index++;
                continue;
            }

            if (isNotImportant(existCalendar)) {
                break;
            }

            ZonedDateTime postponedTime = existCalendarPostponed(existCalendar, newCalendar);

            if (isConflict(postponedTime)) {
                throw new CalendarConflictException("schedule conflict");
            }
        }
        return index;
    }

    private int getIndexToInsertNormalCalendar(List<AutomaticCalendar> automaticCalendarList, AutomaticCalendar newCalendar) {
        int index = 0;

        for (AutomaticCalendar existCalendar : automaticCalendarList) {

            if (isMostImportant(existCalendar, getDifferentTimeBetweenCalendarAndNow(existCalendar)) || isImportant(existCalendar)) {
                index++;
                continue;
            }

            ZonedDateTime postponedTime = existCalendarPostponed(existCalendar, newCalendar);

            if (isConflict(postponedTime)) {
                throw new CalendarConflictException("schedule conflict");
            }
        }
        return index;
    }

    private DifferentTime getDifferentTimeBetweenCalendarAndNow(AutomaticCalendar calendar) {
        ZonedDateTime calendarEndDate = LocalDateTime.parse(calendar.getEndTime(), formatter).atZone(seoul);
        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);
        return DifferentTime.differentTime(now, calendarEndDate);
    }

    private boolean isManualCalendarExistBetweenTwoTime(ZonedDateTime start, ZonedDateTime end, ManualCalendar manualCalendar) {
        ZonedDateTime manualCalendarDate = LocalDateTime.parse(manualCalendar.getStartTime(), formatter).atZone(seoul);;

        long startMilliSecond = start.toInstant().toEpochMilli();
        long manualCalendarMilliSecond = manualCalendarDate.toInstant().toEpochMilli();
        long endMilliSecond = end.toInstant().toEpochMilli();

        return startMilliSecond < manualCalendarMilliSecond && manualCalendarMilliSecond < endMilliSecond;
    }

    private static boolean isMostImportant(AutomaticCalendar calendar, DifferentTime differentTime) {
        return (calendar.getPriority() == 1) || calendar.isParticularImportant() && differentTime.getDays() < 1 && (differentTime.getHours() < 7);
    }

    private static boolean isNotMostImportant(AutomaticCalendar calendar, DifferentTime differentTime) {
        return !isMostImportant(calendar, differentTime);
    }

    private static boolean isImportant(AutomaticCalendar calendar) {
        return calendar.isParticularImportant();
    }

    private static boolean isNotImportant(AutomaticCalendar calendar) {
        return !isImportant(calendar);
    }

    private static boolean isExistingCalendarMoreUrgent(ZonedDateTime newCalendarEndDate, ZonedDateTime calendarEndDate) {
        return DifferentTime.differentTime(newCalendarEndDate, calendarEndDate).getDiff() < 0;
    }

    private boolean isConflict(ZonedDateTime postponedTime) {
        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);
        return DifferentTime.differentTime(now, postponedTime).getDiff() < 0;
    }

    private ZonedDateTime existCalendarPostponed(AutomaticCalendar existCalendar, AutomaticCalendar newCalendar) {
        ZonedDateTime existCalendarEndDate = LocalDateTime.parse(existCalendar.getEndTime(), formatter).atZone(seoul);

        return existCalendarEndDate.minusHours(
                existCalendar.getRequiredTime() + newCalendar.getRequiredTime());
    }

    private ArrayList<AutomaticCalendar> getSortedCalendarList(ArrayList<AutomaticCalendar> automaticCalendarList, AutomaticCalendar newCalendar) {
        DifferentTime differentTime = getDifferentTimeBetweenCalendarAndNow(newCalendar);

        if (isMostImportant(newCalendar, differentTime)) {
            newCalendar.setPriority(1);

            int index = getIndexToInsertMostImportantCalendar(automaticCalendarList, newCalendar);
            automaticCalendarList.add(index, newCalendar);

            return automaticCalendarList;

        } else if (isImportant(newCalendar)) {
            newCalendar.setPriority(2);

            int index = getIndexToInsertImportantCalendar(automaticCalendarList, newCalendar);
            automaticCalendarList.add(index, newCalendar);

            return automaticCalendarList;

        } else {
            newCalendar.setPriority(3);

            int index = getIndexToInsertNormalCalendar(automaticCalendarList, newCalendar);
            automaticCalendarList.add(index, newCalendar);

            return automaticCalendarList;
        }
    }

    @Override
    public AutoToManualCalendar save(AutoToManualCalendar autoToManualCalendar) {
        return autoToManualRepository.save(autoToManualCalendar);
    }

    @Override
    public List<AutoToManualCalendar> saveAll(List<AutoToManualCalendar> autoToManualCalendars) {
        return autoToManualRepository.saveAll(autoToManualCalendars);
    }

    @Override
    public List<AutoToManualCalendar> getAllByDate(String date) {
        return autoToManualRepository.getAutoToManualCalendarsByStartTimeContaining(date);
    }

    @Override
    public void addNewCalendar(AutomaticCalendar newCalendar, String userId) {
//        ArrayList<AutomaticCalendar> automaticCalendarList = automaticCalendarService.getAllByUserId(userId)
//                .orElse(new ArrayList<>());
//        ArrayList<ManualCalendar> manualCalendarList = manualCalendarService.getAllByUserId(userId)
//                .orElse(new ArrayList<>());
//        Time time = timeService.getAllocateTimeByUserId(userId)
//                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid jwt"));
//
//        ArrayList<AutomaticCalendar> sortedAutomaticCalendarList = getSortedCalendarList(automaticCalendarList, newCalendar);
//
//        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);
//
//        ArrayList<AvailableTime> availableTimes = new ArrayList<>();
//        // TODO: 고정 일정 가져와서 avaliableTime의 시작 시간과 끝 시간의 millisecond 사이에 있는지 검증 후 split
//        availableTimes.add(
//                AvailableTime.builder()
//                        .date(now.toString())
//                        .availableTime(time.getEnd() - time.getStart())
//                        .build()
//        );
//
//        for (ManualCalendar manualCalendar : manualCalendarList) {
//            for (AvailableTime availableTime : availableTimes) {
//                if isManualCalendarExistBetweenTwoTime(availableTime.getStartTime(), availableTime.getEndTime(), manualCalendar);
//            }
//            AvailableTime availableTime1
//        }
//        가용시간_클래스_리스트;
//        자동일정_할당된_가용시간_클래스_리스트;
    }

    @Override
    public AutoToManualCalendar test(AutomaticCalendar automaticCalendar) {
        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);

        return this.save(AutoToManualCalendar.builder()
                .auto(automaticCalendar)
                .startTime(now.format(formatter))
                .endTime(now.plusHours(automaticCalendar.getRequiredTime()).format(formatter))
                .build());
    }

    @Override
    public void deleteAllByAutomaticCalendar(AutomaticCalendar automaticCalendar) {
        autoToManualRepository.deleteAllByAuto(automaticCalendar);
    }

    @Override
    @Transactional
    public AutoToManualCalendar testUpdateV2(User user, AutomaticCalendar automaticCalendar) {
        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);
        autoToManualRepository.deleteAllByAutoUser(user);

        // Todo: 모든 항목 삭제 이후 다시 addNewCalendar 메서드 호출로 할당시간 설정~
        return this.save(AutoToManualCalendar.builder()
                .auto(automaticCalendar)
                .startTime(now.format(formatter))
                .endTime(now.plusHours(automaticCalendar.getRequiredTime()).format(formatter))
                .build());
    }
}
