package com.library.libraryapp.controller;

import com.library.libraryapp.model.User;
import com.library.libraryapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // SECURE: Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered securely!";
    }
    @PostMapping("/login")
    public String login() {
        // Spring Security handles the heavy lifting,
        // reaching this method means the user is already authenticated.
        return "User logged in successfully!";
    }
}
