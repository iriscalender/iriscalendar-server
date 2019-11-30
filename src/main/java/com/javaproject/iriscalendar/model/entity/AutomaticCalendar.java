package com.javaproject.iriscalendar.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@Table(name="auto_calendar")
public class AutomaticCalendar {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY)
    @Embedded
    @JsonIgnore
    private User user;

    @NotNull
    private String category;
    @NotNull
    private String calendarName;
    @NotNull
    private String endTime;
    @NotNull
    private int requiredTime;
    @NotNull
    private boolean isParticularImportant;
    @JsonIgnore
    private int priority;
}

