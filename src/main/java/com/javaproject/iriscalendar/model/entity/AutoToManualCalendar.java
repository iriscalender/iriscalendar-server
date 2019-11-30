package com.javaproject.iriscalendar.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@Table(name="auto_to_manual")
public class AutoToManualCalendar {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(targetEntity = AutomaticCalendar.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @Embedded
    private AutomaticCalendar auto;

    @NotNull
    private String startTime;

    @NotNull
    private String endTime;
}


