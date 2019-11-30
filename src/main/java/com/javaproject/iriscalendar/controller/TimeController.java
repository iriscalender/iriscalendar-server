package com.javaproject.iriscalendar.controller;

import com.javaproject.iriscalendar.exception.AlreadyExistException;
import com.javaproject.iriscalendar.exception.InvalidJwtAuthenticationException;
import com.javaproject.iriscalendar.model.entity.Time;
import com.javaproject.iriscalendar.model.entity.User;
import com.javaproject.iriscalendar.model.request.TimeModel;
import com.javaproject.iriscalendar.model.response.TimeResponse;
import com.javaproject.iriscalendar.service.auth.AuthService;
import com.javaproject.iriscalendar.service.time.TimeService;
import com.javaproject.iriscalendar.service.auth.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/time")
public class TimeController {
    @Autowired
    AuthService authService;
    @Autowired
    TimeService timeService;
    @Autowired
    TokenService tokenService;

    @GetMapping({"", "/"})
    public TimeResponse getTime(@RequestHeader("Authorization") String auth) {
        String id = tokenService.getIdentity(auth);

        Time time = timeService.getAllocateTimeByUserId(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));
        return new TimeResponse(time.getStart(), time.getEnd());
    }

    @PostMapping({"", "/"})
    public TimeResponse saveTime(@RequestHeader("Authorization") String auth, @RequestBody @Valid TimeModel timeModel) {
        String id = tokenService.getIdentity(auth);
        Optional<Time> time = timeService.getAllocateTimeByUserId(id);
        if (time.isPresent()) {
            throw new AlreadyExistException("Time already set.");
        }
        User user = authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        Time newTime = Time.builder()
                .user(user)
                .start(timeModel.getStart())
                .end(timeModel.getEnd())
                .build();

        timeService.save(newTime);
        return new TimeResponse(newTime.getStart(), newTime.getEnd());
    }

    @PatchMapping({"", "/"})
    public TimeResponse updateTime(@RequestHeader("Authorization") String auth, @RequestBody @Valid TimeModel partialUpdate) {
        String id = tokenService.getIdentity(auth);

        Time time = timeService.getAllocateTimeByUserId(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        timeService.update(time, partialUpdate);
        return new TimeResponse(time.getStart(), time.getEnd());
    }
}
