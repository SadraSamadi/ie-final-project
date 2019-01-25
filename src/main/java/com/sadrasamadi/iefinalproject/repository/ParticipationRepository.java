package com.sadrasamadi.iefinalproject.repository;

import com.sadrasamadi.iefinalproject.model.Event;
import com.sadrasamadi.iefinalproject.model.Participation;
import com.sadrasamadi.iefinalproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long>,
        JpaSpecificationExecutor<Participation> {

    Participation findByEventAndUser(Event event, User user);

    List<Participation> findAllByEvent(Event event);

    List<Participation> findAllByUser(User user);

}
