package com.sadrasamadi.iefinalproject.repository;

import com.sadrasamadi.iefinalproject.model.Event;
import com.sadrasamadi.iefinalproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByUser(User user);

}
