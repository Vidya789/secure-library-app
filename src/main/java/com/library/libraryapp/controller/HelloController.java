package com.library.libraryapp.controller;// Make sure this matches your actual package name

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String sayHello() {
        return "Hello! Your Library App is running successfully.";
    }
}