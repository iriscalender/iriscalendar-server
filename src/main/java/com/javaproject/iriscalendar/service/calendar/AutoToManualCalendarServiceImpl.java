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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AutoToManualCalendarServiceImpl implements AutoToManualCalendarService {
    private ZoneId seoul = ZoneId.of("Asia/Seoul");

    private String dateFormatString = "yyyy-MM-dd";
    private String formatString = "yyyy-MM-dd HH:mm";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatString);
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormatString);

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
        ZonedDateTime newCalendarEndDate = LocalDateTime.parse(newCalendar.getEndTime(), dateFormatter).atZone(seoul);;

        int index = 0;

        for (AutomaticCalendar existCalendar : automaticCalendarList) {

            ZonedDateTime existingCalendarEndDate = LocalDateTime.parse(existCalendar.getEndTime(), dateFormatter).atZone(seoul);
            DifferentTime differentTime = DifferentTime.differentTime(now, existingCalendarEndDate);

            if (isNotMostImportant(existCalendar, differentTime)) {
                break;
            }
            if (isExistingCalendarMoreUrgent(newCalendarEndDate, existingCalendarEndDate)) {
                index++;
                continue;
            }

            ZonedDateTime postponedTime = existCalendarPostponed(existCalendar, newCalendar);
//            if (isConflict(postponedTime)) {
//                throw new CalendarConflictException("schedule conflict");
//            }
            return index;
        }
        return index;
    }

    private int getIndexToInsertImportantCalendar(List<AutomaticCalendar> automaticCalendarList, AutomaticCalendar newCalendar) {
        int index = 0;
        int requiredTime = 0;

        for (AutomaticCalendar existCalendar : automaticCalendarList) {

            if (isMostImportant(existCalendar, getDifferentTimeBetweenCalendarAndNow(existCalendar))) {
                index++;
                continue;
            }

            if (isNotImportant(existCalendar)) {
                break;
            }

            ZonedDateTime postponedTime = existCalendarPostponed(existCalendar, newCalendar);

//            if (isConflict(postponedTime, requiredTime)) {
//                throw new CalendarConflictException("schedule conflict");
//            }
            return index;
        }
        return index;
    }

    private int getIndexToInsertNormalCalendar(List<AutomaticCalendar> automaticCalendarList, AutomaticCalendar newCalendar) {
        int index = 0;
        int requiredTime = 0;

        for (AutomaticCalendar existCalendar : automaticCalendarList) {
            ZonedDateTime newCalendarEndTime = LocalDate.parse(newCalendar.getEndTime(), dateFormatter).atStartOfDay(seoul);
            ZonedDateTime existCalendarEndTime = LocalDate.parse(existCalendar.getEndTime(), dateFormatter).atStartOfDay(seoul);

            if (((isMostImportant(existCalendar, getDifferentTimeBetweenCalendarAndNow(existCalendar))) || isImportant(existCalendar)) || isExistingCalendarMoreUrgent(newCalendarEndTime, existCalendarEndTime)) {
                index++;
                requiredTime += existCalendar.getRequiredTime();
                continue;
            }

            ZonedDateTime postponedTime = existCalendarPostponed(existCalendar, newCalendar);

//            if (isConflict(postponedTime)) {
//                throw new CalendarConflictException("schedule conflict");
//            }
            return index;
        }
        return index;
    }

    private DifferentTime getDifferentTimeBetweenCalendarAndNow(AutomaticCalendar calendar) {
        LocalDate calendarEndDate = LocalDate.parse(calendar.getEndTime(), dateFormatter);
        ZonedDateTime a = calendarEndDate.atStartOfDay(seoul);
        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);
        return DifferentTime.differentTime(a, now);
    }

    private boolean isManualCalendarExistBetweenTwoTime(ZonedDateTime start, ZonedDateTime end, ManualCalendar manualCalendar) {
        ZonedDateTime manualCalendarStartDate = LocalDateTime.parse(manualCalendar.getStartTime(), formatter).atZone(seoul);
        ZonedDateTime manualCalendarEndDate = LocalDateTime.parse(manualCalendar.getEndTime(), formatter).atZone(seoul);;

        long startMilliSecond = start.toInstant().toEpochMilli();
        long endMilliSecond = end.toInstant().toEpochMilli();
        long manualCalendarStartMilliSecond = manualCalendarStartDate.toInstant().toEpochMilli();
        long manualCalendarEndMilliSecond = manualCalendarEndDate.toInstant().toEpochMilli();

        return (startMilliSecond < manualCalendarStartMilliSecond && manualCalendarStartMilliSecond < endMilliSecond) ||
                (startMilliSecond < manualCalendarEndMilliSecond && manualCalendarEndMilliSecond < endMilliSecond);
    }

    private static boolean isMostImportant(AutomaticCalendar calendar, DifferentTime differentTime) {
        return (calendar.getPriority() == 1) || (calendar.isParticularImportant()) && (differentTime.getDays() <= 1);
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
        return DifferentTime.differentTime(newCalendarEndDate, calendarEndDate).getDiff() > 0;
    }

    private boolean isConflict(ZonedDateTime postponedTime, int requiredTime) {
        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);
        return DifferentTime.differentTime(now, postponedTime).getDiff() < 0;
    }

    private ZonedDateTime existCalendarPostponed(AutomaticCalendar existCalendar, AutomaticCalendar newCalendar) {
        ZonedDateTime existCalendarEndDate = LocalDate.parse(existCalendar.getEndTime(), dateFormatter).atStartOfDay(seoul);
        existCalendarEndDate = existCalendarEndDate.plusDays(1);

        return existCalendarEndDate.minusHours(
                existCalendar.getRequiredTime() + newCalendar.getRequiredTime());
    }

    private ArrayList<AutomaticCalendar> getSortedCalendarList(ArrayList<AutomaticCalendar> automaticCalendarList, AutomaticCalendar newCalendar) {
        DifferentTime differentTime = getDifferentTimeBetweenCalendarAndNow(newCalendar);

        if (isMostImportant(newCalendar, differentTime)) {
            newCalendar.setPriority(1);

            int index = getIndexToInsertMostImportantCalendar(automaticCalendarList, newCalendar);
            automaticCalendarList.add(index, newCalendar);
            automaticCalendarService.save(newCalendar);
            return automaticCalendarList;

        } else if (isImportant(newCalendar)) {
            newCalendar.setPriority(2);

            int index = getIndexToInsertImportantCalendar(automaticCalendarList, newCalendar);
            automaticCalendarList.add(index, newCalendar);
            automaticCalendarService.save(newCalendar);
            return automaticCalendarList;

        } else {
            newCalendar.setPriority(3);

            int index = getIndexToInsertNormalCalendar(automaticCalendarList, newCalendar);
            automaticCalendarList.add(index, newCalendar);
            automaticCalendarService.save(newCalendar);
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
    public Optional<List<AutoToManualCalendar>> getAllByUserIdAndDate(String id, String date) {
        return autoToManualRepository.getAutoToManualCalendarsByAutoUserIdAndStartTimeContaining(id, date);
    }

    private ZonedDateTime getAllocateTimeStringToZonedDateTimeWithDate(String time, ZonedDateTime date) {
        TemporalAccessor parsedTime = timeFormatter.parse(time);
        return Year.of(date.getYear())
                .atMonth(date.getMonthValue())
                .atDay(date.getDayOfMonth())
                .atTime(parsedTime.get(ChronoField.HOUR_OF_DAY), parsedTime.get(ChronoField.MINUTE_OF_HOUR))
                .atZone(seoul);
    }

    @Override
    public void addNewCalendar(AutomaticCalendar newCalendar, String userId) {
        ArrayList<AutomaticCalendar> automaticCalendarList = automaticCalendarService.getAllByUserId(userId)
                .orElse(new ArrayList<>());
        ArrayList<ManualCalendar> manualCalendarList = manualCalendarService.getAllByUserId(userId)
                .orElse(new ArrayList<>());
        Time time = timeService.getAllocateTimeByUserId(userId)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid jwt"));
        autoToManualRepository.deleteAll();

        ArrayList<AutomaticCalendar> sortedAutomaticCalendarList = getSortedCalendarList(automaticCalendarList, newCalendar);
        ZonedDateTime now = LocalDateTime.now(seoul).atZone(seoul);
        ZonedDateTime timeStart = getAllocateTimeStringToZonedDateTimeWithDate(time.getStart(), now);
        ZonedDateTime timeEnd = getAllocateTimeStringToZonedDateTimeWithDate(time.getEnd(), now);

        ArrayList<AvailableTime> availableTimes = new ArrayList<>();
        // TODO: 고정 일정 가져와서 avaliableTime의 시작 시간과 끝 시간의 millisecond 사이에 있는지 검증 후 split

//        for (ManualCalendar manualCalendar : manualCalendarList) {
//            ZonedDateTime calendarStartDate = LocalDateTime.parse(manualCalendar.getStartTime(), formatter).atZone(seoul);
//            ZonedDateTime calendarEndDate = LocalDateTime.parse(manualCalendar.getEndTime(), formatter).atZone(seoul);
//
//            if (calendarStartDate.getDayOfMonth() != calendarEndDate.getDayOfMonth()) {
//                // 시작 시간은 오늘이고 끝 시간은 다른 날짜
//                if (calendarStartDate.getDayOfMonth() == now.getDayOfMonth()) {
//                    AvailableTime.builder()
//                            .date(DateTimeFormatter.ofPattern(dateFormatString).format(now))
//                            .availableTime(DifferentTime.differentTime(now, calendarStartDate))
//                            .startTime(now)
//                            .endTime(calendarStartDate)
//                            .build();
//
//                    AvailableTime.builder()
//                            .date(DateTimeFormatter.ofPattern(dateFormatString).format(now))
//                            .availableTime(DifferentTime.differentTime(calendarEndDate, timeEnd))
//                            .startTime(calendarEndDate)
//                            .endTime(timeEnd)
//                            .build();
//
//
//                } else {
//                    // 시작시간과 끝 시간이 모두 오늘이 아니고 다른 날짜
//                    AvailableTime.builder()
//                            .date(DateTimeFormatter.ofPattern(dateFormatString).format(now))
//                            .availableTime(DifferentTime.differentTime(now, calendarStartDate))
//                            .startTime(calendarStartDate)
//                            .endTime(calendarStartDate)
//                            .build();
//
//                    AvailableTime.builder()
//                            .date(DateTimeFormatter.ofPattern(dateFormatString).format(now))
//                            .availableTime(DifferentTime.differentTime(calendarEndDate, timeEnd))
//                            .startTime(calendarEndDate)
//                            .endTime(timeEnd)
//                            .build();
//                }
//            } else {
//                // 시작 시간과 끝 시간이 오늘이면
//                if (calendarStartDate.getDayOfMonth() == now.getDayOfMonth()) {
//                    availableTimes.add(AvailableTime.builder()
//                            .date(DateTimeFormatter.ofPattern(dateFormatString).format(now))
//                            .availableTime(DifferentTime.differentTime(now, calendarStartDate))
//                            .startTime(now)
//                            .endTime(calendarStartDate)
//                            .build());
//
//                    availableTimes.add(AvailableTime.builder()
//                            .date(DateTimeFormatter.ofPattern(dateFormatString).format(now))
//                            .availableTime(DifferentTime.differentTime(calendarEndDate, timeEnd))
//                            .startTime(calendarEndDate)
//                            .endTime(timeEnd)
//                            .build());
//                } else {
//                    // 시작 시간과 끝 시간이 오늘이 아니면
//                    timeStartWithDate = DateTimeFormatter.ofPattern(dateFormatString).format(timeStart);
//                    availableTimes.add(AvailableTime.builder()
//                            .date(DateTimeFormatter.ofPattern(dateFormatString).format(timeStart))
//                            .availableTime(DifferentTime.differentTime(timeStart, calendarStartDate))
//                            .startTime(timeStart)
//                            .endTime(calendarStartDate)
//                            .build());
//
//                    availableTimes.add(AvailableTime.builder()
//                            .date(DateTimeFormatter.ofPattern(dateFormatString).format(now))
//                            .availableTime(DifferentTime.differentTime(calendarEndDate, timeEnd))
//                            .startTime(calendarEndDate)
//                            .endTime(timeEnd)
//                            .build());
//                }
//            }
//
//            for (AvailableTime availableTime : availableTimes) {
//                if (isManualCalendarExistBetweenTwoTime(availableTime.getStartTime(), availableTime.getEndTime(), manualCalendar)) {
//
//                }
//            }
//        }

        if (availableTimes.isEmpty())
            availableTimes.add(AvailableTime.builder()
                .date(DateTimeFormatter.ofPattern(dateFormatString).format(now))
                .availableTime(DifferentTime.differentTime(timeStart, timeEnd))
                .startTime(timeStart)
                .endTime(timeEnd)
                .build());

        ArrayList<AutoToManualCalendar> autoToManualCalendarList = new ArrayList<>();

        for (AutomaticCalendar automaticCalendar : sortedAutomaticCalendarList) {
            int availableTimeIndex = 0;
            int dayCount = 1;
            long remainTime;
            while (!availableTimes.isEmpty()) {
                AvailableTime availableTime = availableTimes.get(availableTimeIndex);
                remainTime = Math.abs(availableTime.getAvailableTime().getHours()) - automaticCalendar.getRequiredTime();
                int calendarRemainTime = automaticCalendar.getRequiredTime();

                if (remainTime >= 0) {
                    autoToManualCalendarList.add(AutoToManualCalendar.builder()
                            .auto(automaticCalendar)
                            .startTime(availableTime.getStartTime().format(DateTimeFormatter.ofPattern(formatString)))
                            .endTime(availableTime.getEndTime().minusHours(remainTime).format(DateTimeFormatter.ofPattern(formatString)))
                            .build());

                    availableTime.setAvailableTime(DifferentTime.differentTime(availableTime.getEndTime().plusHours(remainTime), availableTime.getEndTime()));
                    availableTime.setStartTime(availableTime.getStartTime().plusHours(calendarRemainTime));
                    availableTime.setEndTime(availableTime.getEndTime());

                    calendarRemainTime = availableTime.getStartTime().plusHours(remainTime).getHour() - availableTime.getEndTime().minusHours(calendarRemainTime).getHour() - calendarRemainTime;
                }

                if (remainTime < 0) {
                    if (availableTimes.isEmpty()) {
                        availableTimes.add(AvailableTime.builder()
                                .date(DateTimeFormatter.ofPattern(dateFormatString).format(now.plusDays(dayCount)))
                                .availableTime(DifferentTime.differentTime(timeStart, timeEnd))
                                .startTime(timeStart.plusDays(dayCount))
                                .endTime(timeEnd.plusDays(dayCount))
                                .build());
                        dayCount++;
                    }
                }
                if (calendarRemainTime == 0 || remainTime == 0) break;
            }
        }

        autoToManualRepository.saveAll(autoToManualCalendarList);
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
