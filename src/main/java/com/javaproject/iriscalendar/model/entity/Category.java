package com.javaproject.iriscalendar.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Entity
@Builder
@DynamicInsert
@Table(name="category")
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonIgnore
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY)
    @Embedded
    @JsonIgnore
    private User user;

    @Column(columnDefinition = "varchar(255) default '기타'")
    private String purple;

    @Column(columnDefinition = "varchar(255) default '회의, 미팅'")
    private String blue;

    @Column(columnDefinition = "varchar(255) default '운동'")
    private String pink;

    @Column(columnDefinition = "varchar(255) default '과제, 공부'")
    private String orange;

    public static class CategoryBuilder {
        private String purple = "기타";
        private String blue = "회의, 미팅";
        private String pink = "운동";
        private String orange = "과제, 공부";
    }
}
