package com.javaproject.iriscalendar.model.request;

import com.javaproject.iriscalendar.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
@Builder
@AllArgsConstructor
public class CategoryModel {
    @NotNull
    private String purple;
    @NotNull
    private String blue;
    @NotNull
    private String pink;
    @NotNull
    private String orange;
}
