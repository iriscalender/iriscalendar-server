package com.javaproject.iriscalendar.controller;

import com.javaproject.iriscalendar.exception.InvalidJwtAuthenticationException;
import com.javaproject.iriscalendar.exception.NoMatchException;
import com.javaproject.iriscalendar.model.entity.*;
import com.javaproject.iriscalendar.model.request.ManualCalendarModel;
import com.javaproject.iriscalendar.model.response.BookedCalendarResponse;
import com.javaproject.iriscalendar.model.response.CalendarByDateResponse;
import com.javaproject.iriscalendar.service.auth.AuthService;
import com.javaproject.iriscalendar.service.calendar.AutoToManualCalendarService;
import com.javaproject.iriscalendar.service.calendar.AutomaticCalendarService;
import com.javaproject.iriscalendar.service.calendar.ManualCalendarService;
import com.javaproject.iriscalendar.service.time.TimeService;
import com.javaproject.iriscalendar.service.auth.TokenService;
import com.javaproject.iriscalendar.util.BookedCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    @PersistenceContext
    EntityManager em;

    @Autowired
    AuthService authService;
    @Autowired
    TimeService timeService;
    @Autowired
    TokenService tokenService;
    @Autowired
    AutomaticCalendarService automaticCalendarService;
    @Autowired
    ManualCalendarService manualCalendarService;
    @Autowired
    AutoToManualCalendarService autoToManualCalendarService;

    @PostMapping("/auto")
    public ResponseEntity<AutomaticCalendar> addAutomaticCalendar(@RequestHeader("Authorization") String auth, @RequestBody @Valid AutomaticCalendar newCalendar) {
        String id = tokenService.getIdentity(auth);
        User user = authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        newCalendar.setUser(user);
        newCalendar.setPriority(0);
        autoToManualCalendarService.addNewCalendar(newCalendar, id);
        automaticCalendarService.save(newCalendar);

        autoToManualCalendarService.test(newCalendar);
        return new ResponseEntity<>(newCalendar, HttpStatus.CREATED);
    }

    @GetMapping("/auto/{id}")
    public AutomaticCalendar getAutomaticCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long idx) {
        String id = tokenService.getIdentity(auth);
        authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        return automaticCalendarService.getAutomaticCalendarByIdx(idx)
                .orElseThrow(() -> new NoMatchException("no match automatic calendar"));
    }

    @PatchMapping("/auto/{id}")
    @Transactional
    public AutomaticCalendar updateAutomaticCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long idx, @RequestBody @Valid AutomaticCalendar update) {
        String id = tokenService.getIdentity(auth);
        authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        AutomaticCalendar calendar = automaticCalendarService.getAutomaticCalendarByIdx(idx)
                .orElseThrow(() -> new NoMatchException("no match automatic calendar"));

        autoToManualCalendarService.deleteAllByAutomaticCalendar(calendar);

        AutomaticCalendar updatedCalendar = automaticCalendarService.update(calendar, update);
        autoToManualCalendarService.test(updatedCalendar);
        return updatedCalendar;
    }

    @DeleteMapping("/auto/{id}")
    public ResponseEntity<String> deleteAutomaticCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long idx) {
        String id = tokenService.getIdentity(auth);
        authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        AutomaticCalendar calendar = automaticCalendarService.getAutomaticCalendarByUserIdAndIdx(id, idx)
                .orElseThrow(() -> new NoMatchException("no match automatic calendar"));

        automaticCalendarService.delete(calendar);
        return ResponseEntity.ok("");
    }

    @PostMapping("/manual")
    public ResponseEntity<ManualCalendar> addManualCalendar(@RequestHeader("Authorization") String auth, @RequestBody @Valid ManualCalendar newCalendar) {
        String id = tokenService.getIdentity(auth);
        User user = authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        newCalendar.setUser(user);

        /* Todo: 삽입하려는 시간대에 배치된 자동일정이 있는지 확인
        있으면 기존 배치된 시간은
        시작: 기존 / 끝 : 삽입하려는 일정의 시작시간
        시작: 삽입하려는 일정의 끝시간 / 끝: 기존 + 삽입하려는 일정의 필요한 시간
        ( 필요하려는 시간 = 일정끝시간 - 일정시작시간)

        일정 필요한 시간을 변수로 두고
        뒤의 모든 일정의 시작시간과 끝 시간에 +3시간
        isConflict 계속 돌려주고
        더햇을 때 일정할당가능시간의 끝을 초과하면 초과하는 만큼 내일 추가해주기
        */

        return new ResponseEntity<>(manualCalendarService.save(newCalendar), HttpStatus.CREATED);
    }

    @GetMapping("/manual/{id}")
    public ManualCalendar getManualCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long calendarId) {
        String id = tokenService.getIdentity(auth);
        authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        return manualCalendarService.getManualCalendarByIdx(calendarId)
                .orElseThrow(() -> new NoMatchException("no match calendar"));
    }

    @PatchMapping("/manual/{id}")
    public ManualCalendar updateManualCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long calendarId, @RequestBody @Valid ManualCalendarModel update) {
        String id = tokenService.getIdentity(auth);
        authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        ManualCalendar calendar = manualCalendarService.getManualCalendarByIdx(calendarId)
                .orElseThrow(() -> new NoMatchException("no match calendar"));

        return manualCalendarService.update(calendar, update);
    }

    @DeleteMapping("/manual/{id}")
    public ResponseEntity<String> deleteManualCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long calendarId) {
        String id = tokenService.getIdentity(auth);
        authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        ManualCalendar calendar = manualCalendarService.getManualCalendarByIdx(calendarId)
                .orElseThrow(() -> new NoMatchException("no match calendar"));

        manualCalendarService.delete(calendar);
        return ResponseEntity.ok("");
    }

    @GetMapping("/{date}")
    public CalendarByDateResponse getCalendarByDate(@RequestHeader("Authorization") String auth, @PathVariable("date") String date) throws Exception {

        String id = tokenService.getIdentity(auth);
        authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        List<AutoToManualCalendar> autoToManualCalendars = autoToManualCalendarService.getAllByUserIdAndDate(id, date)
                .orElse(new ArrayList<>());
        List<ManualCalendar> manualCalendars = manualCalendarService.getAllByUserIdAndDate(id, date)
                .orElse(new ArrayList<>());

        return new CalendarByDateResponse(manualCalendars, autoToManualCalendars);
    }
    @GetMapping("/book")
    public BookedCalendarResponse getBookedCalendar(@RequestHeader("Authorization") String auth) throws IOException {
        String id = tokenService.getIdentity(auth);
        User user = authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        String sql = "SELECT DISTINCT auto_calendar.category, substring(auto_to_manual.start_time, 1, 10) AS date " +
                "FROM auto_to_manual inner join auto_calendar ON auto_calendar.idx = auto_to_manual.auto_idx AND auto_calendar.user_idx = ?1 " +
                "UNION " +
                "SELECT DISTINCT manual_calendar.category, substring(manual_calendar.start_time, 1, 10) AS date " +
                "FROM manual_calendar " +
                "WHERE manual_calendar.user_idx = ?1";

        Query query = em.createNativeQuery(sql);
        query.setParameter(1, user.getIdx());

        List<Object[]> bookedCalendars = query.getResultList();
        List<BookedCalendar> calendars = bookedCalendars.stream().map(calendar -> new BookedCalendar(
                (String)calendar[0],
                (String)calendar[1])).collect(Collectors.toList());

        return new BookedCalendarResponse(calendars);

    }
}
