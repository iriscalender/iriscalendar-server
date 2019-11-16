package com.javaproject.iriscalendar.service;

import com.javaproject.iriscalendar.model.entity.Time;
import com.javaproject.iriscalendar.model.request.TimeModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface TimeService {
    Optional<Time> getAllocateTimeByUserId(String id);
    Time save(Time time);
    Time update(Time time, TimeModel partialUpdate);
}
