package com.library.libraryapp.controller;

import com.library.libraryapp.model.BorrowRequest;
import com.library.libraryapp.model.User;
import com.library.libraryapp.repository.BorrowRequestRepository;
import com.library.libraryapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
public class BorrowRequestController {

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{bookId}")
    public String createBorrowRequest(@PathVariable Long bookId, Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BorrowRequest request = new BorrowRequest();
        request.setUserId(user.getId());
        request.setBookId(bookId);
        request.setStatus("PENDING");

        borrowRequestRepository.save(request);
        return "Borrow request submitted successfully!";
    }

    @GetMapping("/my")
    public List<BorrowRequest> myRequests(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return borrowRequestRepository.findByUserId(user.getId());
    }

    @GetMapping("/all")
    public List<BorrowRequest> allRequests() {
        return borrowRequestRepository.findAll();
    }

    @PutMapping("/{id}/approve")
    public String approveRequest(@PathVariable Long id) {
        BorrowRequest request = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));

        request.setStatus("APPROVED");
        borrowRequestRepository.save(request);
        return "Approved!";
    }

    @PutMapping("/{id}/reject")
    public String rejectRequest(@PathVariable Long id) {
        BorrowRequest request = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow request not found"));

        request.setStatus("REJECTED");
        borrowRequestRepository.save(request);
        return "Rejected!";
    }
}