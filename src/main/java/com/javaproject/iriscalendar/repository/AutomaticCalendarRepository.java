package com.javaproject.iriscalendar.repository;

import com.javaproject.iriscalendar.model.entity.AutomaticCalendar;
import com.javaproject.iriscalendar.model.entity.ManualCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface AutomaticCalendarRepository extends JpaRepository<AutomaticCalendar, String> {
    Optional<AutomaticCalendar> findAutomaticCalendarByUserId(String id);
    Optional<ArrayList<AutomaticCalendar>> findAllByUserIdOrderByEndTimeAscPriorityAsc(String id);
    Optional<AutomaticCalendar> findAutomaticCalendarByIdx(Long idx);
    Optional<AutomaticCalendar> findAutomaticCalendarByUserIdAndIdx(String id, Long idx);
}
