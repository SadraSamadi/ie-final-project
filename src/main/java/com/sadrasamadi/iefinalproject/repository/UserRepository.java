package com.sadrasamadi.iefinalproject.repository;

import com.sadrasamadi.iefinalproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByRole(User.Role role);

    boolean existsByEmail(String email);

    User findByEmail(String email);

}
