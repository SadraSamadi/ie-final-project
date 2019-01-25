package com.sadrasamadi.iefinalproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue
    private long id;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @OneToOne
    @JoinColumn
    private User receiver;

    @OneToOne
    @JoinColumn
    private User sender;

    @Column(columnDefinition = "text")
    private String text;

}
