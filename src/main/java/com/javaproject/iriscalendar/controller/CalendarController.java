package com.javaproject.iriscalendar.controller;

import com.javaproject.iriscalendar.exception.AlreadyExistException;
import com.javaproject.iriscalendar.exception.InvalidJwtAuthenticationException;
import com.javaproject.iriscalendar.exception.NoMatchException;
import com.javaproject.iriscalendar.model.entity.*;
import com.javaproject.iriscalendar.model.request.AutomaticCalendarModel;
import com.javaproject.iriscalendar.model.request.ManualCalendarModel;
import com.javaproject.iriscalendar.model.response.TimeResponse;
import com.javaproject.iriscalendar.service.auth.AuthService;
import com.javaproject.iriscalendar.service.calendar.AutoToManualCalendarService;
import com.javaproject.iriscalendar.service.calendar.AutomaticCalendarService;
import com.javaproject.iriscalendar.service.calendar.ManualCalendarService;
import com.javaproject.iriscalendar.service.time.TimeService;
import com.javaproject.iriscalendar.service.auth.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
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
    public AutomaticCalendar addAutomaticCalendar(@RequestHeader("Authorization") String auth, @RequestBody @Valid AutomaticCalendar newCalendar) {
        /*
        {
            "category": "purple",
            "calendarName": "그램 회의",
            "endTime": "2018-07-26",
            "requiredTime": 1,
            "isParticularImportant": true
        }
        */
        String id = tokenService.getIdentity(auth);
        User user = authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        newCalendar.setUser(user);
        newCalendar.setPriority(0);
//        autoToManualCalendarService.addNewCalendar(newCalendar, id);
        automaticCalendarService.save(newCalendar);

        autoToManualCalendarService.test(newCalendar);
        return newCalendar;
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
    public ResponseEntity deleteAutomaticCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long idx) {
        String id = tokenService.getIdentity(auth);
        authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        AutomaticCalendar calendar = automaticCalendarService.getAutomaticCalendarByUserIdAndIdx(id, idx)
                .orElseThrow(() -> new NoMatchException("no match automatic calendar"));

        automaticCalendarService.delete(calendar);
        return ResponseEntity.ok("");
    }

//    @PostMapping("/manual")
//    public ManualCalendar addManualCalendar(@RequestHeader("Authorization") String auth, @RequestBody @Valid ManualCalendarModel manualCalendarModel) {
//        /*
//        {
//            "category": "purple",
//            "calendarName": "그램 회의",
//            "startTime": "2018-07-26 13:00",
//            "endTime": "2018-07-26 17:00"
//        }
//        */
//        String id = tokenService.getIdentity(auth);
//        Optional<ManualCalendar> calendar = manualCalendarService.getManualCalendarByUserId(id);
//        if (calendar.isPresent()) {
//            throw new AlreadyExistException("Calendar already set.");
//        }
//        User user = authService.getUserById(id)
//                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));
//
//        ManualCalendar newCalendar = ManualCalendar.builder()
//                .user(user)
//                .category(manualCalendarModel.getCategory())
//                .calendarName(manualCalendarModel.getCalendarName())
//                .startTime(manualCalendarModel.getStartTime())
//                .endTime(manualCalendarModel.getEndTime())
//                .build();
//
//        return manualCalendarService.save(newCalendar);
//    }
//
//    @GetMapping("/manual/{id}")
//    public ManualCalendar getManualCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long calendarId) {
//        String id = tokenService.getIdentity(auth);
//        authService.getUserById(id)
//                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));
//
//        return manualCalendarService.getManualCalendarByIdx(calendarId)
//                .orElseThrow(() -> new NoMatchException("no match calendar"));
//    }
//
//    @PatchMapping("/manual/{id}")
//    public ManualCalendar updateManualCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long calendarId, @RequestBody @Valid ManualCalendarModel update) {
//        String id = tokenService.getIdentity(auth);
//        authService.getUserById(id)
//                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));
//
//        ManualCalendar calendar = manualCalendarService.getManualCalendarByIdx(calendarId)
//                .orElseThrow(() -> new NoMatchException("no match calendar"));
//
//        return manualCalendarService.update(calendar, update);
//    }
//
//    @DeleteMapping("/manual/{id}")
//    public ResponseEntity deleteManualCalendar(@RequestHeader("Authorization") String auth, @PathVariable("id") Long calendarId) {
//        String id = tokenService.getIdentity(auth);
//        authService.getUserById(id)
//                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));
//
//        ManualCalendar calendar = manualCalendarService.getManualCalendarByIdx(calendarId)
//                .orElseThrow(() -> new NoMatchException("no match calendar"));
//
//        manualCalendarService.delete(calendar);
//        return ResponseEntity.ok("");
//    }
//
//    @GetMapping("/{date}")
//    public ResponseEntity getCalendarByDate(@PathVariable("date") String date) {
//        /*
//        [
//            {
//                "category": "purple",
//                "calendarName": "그램 회의",
//                "startTime": "2018-07-26 03:42",
//                "endTime": "2018-07-26 00:42"
//            }
//        ]
//         */
//    }
//    @GetMapping("/book")
//    public ResponseEntity getBookedCalendar(@RequestHeader("Authorization") String auth, @PathVariable Integer id) {
//        /*
//        [
//            {
//                "category": "purple",
//                "date": "2019-01-01"
//            }
//        ]
//         */
//    }
}
