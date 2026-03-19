package com.library.libraryapp.controller;

import com.library.libraryapp.dto.RegisterRequest;
import com.library.libraryapp.exception.BadRequestException;
import com.library.libraryapp.model.User;
import com.library.libraryapp.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String ROLE_USER = "USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists.");
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(ROLE_USER);

        userRepository.save(user);
        logger.info("New user registered: {}", user.getUsername());

        return "User registered securely!";
    }

    @GetMapping("/me")
    public Map<String, String> currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new BadRequestException("User is not authenticated.");
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Authenticated user not found."));

        return Map.of(
                "username", user.getUsername(),
                "role", user.getRole()
        );
    }
}
