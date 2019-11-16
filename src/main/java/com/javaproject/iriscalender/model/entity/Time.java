package com.javaproject.iriscalender.model.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Entity
@Builder
@Table(name="time")
public class Time {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY)
    @Embedded
    private User user;

    private String start;
    private String end;

}