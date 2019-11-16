package com.javaproject.iriscalender.service;

import com.javaproject.iriscalender.exception.InvalidJwtAuthenticationException;
import com.javaproject.iriscalender.model.entity.Time;
import com.javaproject.iriscalender.model.entity.User;
import com.javaproject.iriscalender.model.request.TimeModel;
import com.javaproject.iriscalender.repository.TimeRepository;
import org.graalvm.compiler.nodes.calc.IntegerDivRemNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TimeServiceImpl implements TimeService {
    @Autowired
    TimeRepository timeRepository;

    @Override
    public Time save(Time time) {
        return timeRepository.save(time);
    }

    @Override
    public Optional<Time> getAllocateTimeByUserId(String id) {
        return timeRepository.findTimeByUserId(id);
    }

    @Override
    public Time update(Time time, TimeModel partialUpdate) {
        time.setStart(partialUpdate.getStart());
        time.setEnd(partialUpdate.getEnd());

        return this.save(time);
    }
}
