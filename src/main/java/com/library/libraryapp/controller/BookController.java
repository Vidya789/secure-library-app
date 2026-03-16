package com.library.libraryapp.controller;

import com.library.libraryapp.model.Book;
import com.library.libraryapp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getBooks() {
        // Now it returns the actual list from the database!
        return bookRepository.findAll();
    }

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        // This saves the book you send in the request body
        return bookRepository.save(book);
    }
}