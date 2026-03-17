package com.library.libraryapp.controller;

import com.library.libraryapp.model.User;
import com.library.libraryapp.repository.UserRepository; // Import is good!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository; // <--- ADD THIS LINE!

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
        return "User logged in successfully!";
    }
}