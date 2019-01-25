package com.sadrasamadi.iefinalproject.repository;

import com.sadrasamadi.iefinalproject.model.Message;
import com.sadrasamadi.iefinalproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySender(User sender);

    List<Message> findAllByReceiverOrderByDateDesc(User receiver);

}
