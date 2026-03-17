package com.library.libraryapp.model;

import jakarta.persistence.*;

@Entity
public class BorrowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long bookId;
    private String status;

    // Add these setters and getters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public void setStatus(String status) { this.status = status; }

    public String getStatus() { return status; }
}