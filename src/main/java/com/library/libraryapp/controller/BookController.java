package com.library.libraryapp.controller;

import com.library.libraryapp.exception.BadRequestException;
import com.library.libraryapp.exception.ResourceNotFoundException;
import com.library.libraryapp.model.Book;
import com.library.libraryapp.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam @Size(min = 1, max = 80) String keyword) {
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            throw new BadRequestException("Keyword cannot be blank.");
        }

        return bookRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(trimmed, trimmed);
    }

    @PostMapping
    public Book addBook(@Valid @RequestBody Book book) {
        logger.info("Admin added book: {}", book.getTitle());
        return bookRepository.save(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @Valid @RequestBody Book updatedBook) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        book.setTitle(updatedBook.getTitle().trim());
        book.setAuthor(updatedBook.getAuthor().trim());
        book.setCategory(updatedBook.getCategory().trim());

        logger.info("Admin updated book with id {}", id);
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found");
        }

        bookRepository.deleteById(id);
        logger.info("Admin deleted book with id {}", id);
        return "Book deleted successfully!";
    }
}