package com.javaproject.iriscalendar.service;

import com.javaproject.iriscalendar.model.entity.Category;
import com.javaproject.iriscalendar.model.entity.User;
import com.javaproject.iriscalendar.model.request.CategoryModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CategoryService {
    Optional<Category> getCategoryByUserId(String id);
    Category createDefaultCategory(User user);
    Category update(Category category, CategoryModel categoryModel);
}
