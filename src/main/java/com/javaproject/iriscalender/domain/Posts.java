package com.javaproject.iriscalender.domain;

import com.javaproject.iriscalender.domain.BaseTimeEntity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

/*
@NoArgsConstructor
Entity 클래스를 프로젝트 코드상에서 기본생성자로 생성하는 것은 막되,
JPA에서 Entity 클래스를 생성하는 것은 허용하기 위해 추가

@Getter
클래스 내 모든 필드의 Getter 메소드를 자동생성
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Builder
@Entity
public class Posts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;
}