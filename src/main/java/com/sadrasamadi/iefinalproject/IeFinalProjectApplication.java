package com.sadrasamadi.iefinalproject;

import com.sadrasamadi.iefinalproject.model.User;
import com.sadrasamadi.iefinalproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
public class IeFinalProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(IeFinalProjectApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByRole(User.Role.ADMIN)) {
                User admin = User.builder()
                        .name("کاربر ادمین")
                        .phone("+989123456789")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .avatar("/img/avatar.jpg")
                        .role(User.Role.ADMIN)
                        .build();
                admin = userRepository.save(admin);
                log.info("user created: " + admin);
            }
            if (!userRepository.existsByRole(User.Role.USER)) {
                User user = User.builder()
                        .name("کاربر معمولی")
                        .phone("+989123456789")
                        .email("user@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .avatar("/img/avatar.jpg")
                        .role(User.Role.USER)
                        .build();
                user = userRepository.save(user);
                log.info("user created: " + user);
            }
            log.info("http://localhost:8080/");
        };
    }

}
