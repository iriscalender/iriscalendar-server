package com.javaproject.iriscalendar.controller;

import com.javaproject.iriscalendar.exception.InvalidJwtAuthenticationException;
import com.javaproject.iriscalendar.model.entity.Category;
import com.javaproject.iriscalendar.model.entity.User;
import com.javaproject.iriscalendar.model.request.CategoryModel;
import com.javaproject.iriscalendar.service.AuthService;
import com.javaproject.iriscalendar.service.CategoryService;
import com.javaproject.iriscalendar.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    AuthService authService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    TokenService tokenService;

    @GetMapping({"", "/"})
    public Category getCategory(@RequestHeader("Authorization") String auth) {
        String id = tokenService.getIdentity(auth);

        User user = authService.getUserById(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        return categoryService.getCategoryByUserId(id)
                .orElseGet(() -> categoryService.createDefaultCategory(user));
    }

    @PatchMapping({"", "/"})
    public Category updateCategory(@RequestHeader("Authorization") String auth, @RequestBody @Valid CategoryModel update) {
        String id = tokenService.getIdentity(auth);

        Category category = categoryService.getCategoryByUserId(id)
                .orElseThrow(() -> new InvalidJwtAuthenticationException("invalid JWT token"));

        return categoryService.update(category, update);
    }
}