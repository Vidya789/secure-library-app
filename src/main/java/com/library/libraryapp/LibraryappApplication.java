package com.library.libraryapp;

import com.library.libraryapp.model.User;
import com.library.libraryapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LibraryappApplication {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_ROLE = "ADMIN";

    public static void main(String[] args) {
        SpringApplication.run(LibraryappApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username:" + DEFAULT_ADMIN_USERNAME + "}") String adminUsername,
            @Value("${app.admin.password:}") String adminPassword,
            @Value("${app.admin.role:" + DEFAULT_ADMIN_ROLE + "}") String adminRole) {
        return args -> {
            if (adminPassword == null || adminPassword.isBlank()) {
                return;
            }

            if (!userRepository.existsByUsername(adminUsername)) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
        };
    }
}
