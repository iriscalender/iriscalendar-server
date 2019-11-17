package com.javaproject.iriscalendar.repository;

import com.javaproject.iriscalendar.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findCategoryByUserId(String id);
}
