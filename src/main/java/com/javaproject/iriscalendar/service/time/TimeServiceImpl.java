package com.javaproject.iriscalendar.service.time;

import com.javaproject.iriscalendar.model.entity.Time;
import com.javaproject.iriscalendar.model.request.TimeModel;
import com.javaproject.iriscalendar.repository.TimeRepository;
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
