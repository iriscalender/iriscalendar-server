package com.javaproject.iriscalendar.service;

import com.javaproject.iriscalendar.model.entity.Category;
import com.javaproject.iriscalendar.model.entity.User;
import com.javaproject.iriscalendar.model.request.CategoryModel;
import com.javaproject.iriscalendar.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Optional<Category> getCategoryByUserId(String id) {
        return categoryRepository.findCategoryByUserId(id);
    }

    @Override
    public Category createDefaultCategory(User user) {
        return categoryRepository.save(
                Category.builder()
                        .user(user)
                        .build()
        );
    }

    @Override
    public Category update(Category category, CategoryModel partialUpdate) {
        category.setPurple(partialUpdate.getPurple());
        category.setBlue(partialUpdate.getBlue());
        category.setPink(partialUpdate.getPink());
        category.setOrange(partialUpdate.getOrange());

        return categoryRepository.save(category);
    }
}
