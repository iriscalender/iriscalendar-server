package com.javaproject.iriscalender.service;

import com.javaproject.iriscalender.model.entity.Time;
import com.javaproject.iriscalender.model.entity.User;
import com.javaproject.iriscalender.model.request.TimeModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface TimeService {
    Optional<Time> getAllocateTimeByUserId(String id);
    Time save(Time time);
    Time update(Time time, TimeModel partialUpdate);
}
