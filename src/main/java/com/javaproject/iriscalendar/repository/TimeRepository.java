package com.javaproject.iriscalendar.repository;

import com.javaproject.iriscalendar.model.entity.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeRepository extends JpaRepository<Time, String> {
    Optional<Time> findTimeByUserId(String id);
}