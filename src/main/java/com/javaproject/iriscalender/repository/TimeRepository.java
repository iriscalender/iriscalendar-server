package com.javaproject.iriscalender.repository;

import com.javaproject.iriscalender.model.entity.Time;
import com.javaproject.iriscalender.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimeRepository extends JpaRepository<Time, String> {
    Optional<Time> findTimeByUserId(String id);
}