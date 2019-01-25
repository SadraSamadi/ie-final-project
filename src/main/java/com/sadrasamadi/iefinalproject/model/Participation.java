package com.sadrasamadi.iefinalproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Participation {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    @JoinColumn
    private Event event;

    @OneToOne
    @JoinColumn
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {

        PENDING,

        ACCEPTED,

        DENIED

    }

}
