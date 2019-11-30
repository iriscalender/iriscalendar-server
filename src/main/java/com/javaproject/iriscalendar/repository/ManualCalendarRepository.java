package com.javaproject.iriscalendar.repository;

import com.javaproject.iriscalendar.model.entity.ManualCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ManualCalendarRepository extends JpaRepository<ManualCalendar, String> {
    Optional<ManualCalendar> findManualCalendarByUserId(String id);
    Optional<ArrayList<ManualCalendar>> findAllByUserIdOrderByStartTimeAsc(String id);
    Optional<ManualCalendar> findManualCalendarByIdx(Long idx);
    Optional<List<ManualCalendar>> getManualCalendarsByUserIdAndStartTimeContaining(String id, String date);
}
