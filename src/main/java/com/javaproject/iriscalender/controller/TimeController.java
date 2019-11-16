package com.javaproject.iriscalender.controller;

import com.javaproject.iriscalender.exception.AlreadyExistException;
import com.javaproject.iriscalender.exception.InvalidJwtAuthenticationException;
import com.javaproject.iriscalender.model.entity.Time;
import com.javaproject.iriscalender.model.entity.User;
import com.javaproject.iriscalender.model.request.TimeModel;
import com.javaproject.iriscalender.model.response.TimeResponse;
import com.javaproject.iriscalender.service.AuthService;
import com.javaproject.iriscalender.service.TimeService;
import com.javaproject.iriscalender.service.TokenService;

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
