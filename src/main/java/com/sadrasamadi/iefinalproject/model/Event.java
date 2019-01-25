package com.sadrasamadi.iefinalproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue
    private long id;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @OneToOne
    @JoinColumn
    private User user;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "لطفا تاریخ شروع را انتخاب کنید")
    private LocalDateTime start;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "لطفا تاریخ پایان را انتخاب کنید")
    private LocalDateTime finish;

    @Column
    @NotEmpty(message = "لطفا مکان را وارد کنید")
    private String location;

    @Column
    @NotEmpty(message = "لطفا عنوان را وارد کنید")
    private String title;

    @Column(columnDefinition = "text")
    @NotEmpty(message = "لطفا توضیحات را وارد کنید")
    private String description;

    @Column
    private String poster;

}
