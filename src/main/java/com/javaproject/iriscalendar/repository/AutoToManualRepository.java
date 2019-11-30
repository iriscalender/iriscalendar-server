package com.javaproject.iriscalendar.repository;

import com.javaproject.iriscalendar.model.entity.AutoToManualCalendar;
import com.javaproject.iriscalendar.model.entity.AutomaticCalendar;
import com.javaproject.iriscalendar.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface AutoToManualRepository extends JpaRepository<AutoToManualCalendar, String> {
    Optional<List<AutoToManualCalendar>> getAutoToManualCalendarsByAutoUserIdAndStartTimeContaining(String id, String date);
    void deleteAllByAuto(AutomaticCalendar automaticCalendar);
    void deleteAllByAutoUser(User user);
}
