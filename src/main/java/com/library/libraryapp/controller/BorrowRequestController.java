package com.library.libraryapp.controller;

import com.library.libraryapp.exception.ResourceNotFoundException;
import com.library.libraryapp.model.BorrowRequest;
import com.library.libraryapp.model.User;
import com.library.libraryapp.repository.BorrowRequestRepository;
import com.library.libraryapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
public class BorrowRequestController {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final BorrowRequestRepository borrowRequestRepository;
    private final UserRepository userRepository;

    public BorrowRequestController(BorrowRequestRepository borrowRequestRepository, UserRepository userRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{bookId}")
    public String createBorrowRequest(@PathVariable Long bookId, Authentication authentication) {
        User user = findUser(authentication);

        BorrowRequest request = new BorrowRequest();
        request.setUserId(user.getId());
        request.setBookId(bookId);
        request.setStatus(STATUS_PENDING);

        borrowRequestRepository.save(request);
        return "Borrow request submitted successfully!";
    }

    @GetMapping("/my")
    public List<BorrowRequest> myRequests(Authentication authentication) {
        User user = findUser(authentication);
        return borrowRequestRepository.findByUserId(user.getId());
    }

    @GetMapping("/all")
    public List<BorrowRequest> allRequests() {
        return borrowRequestRepository.findAll();
    }

    @PutMapping("/{id}/approve")
    public String approveRequest(@PathVariable Long id) {
        BorrowRequest request = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found"));

        request.setStatus(STATUS_APPROVED);
        borrowRequestRepository.save(request);
        return "Approved!";
    }

    @PutMapping("/{id}/reject")
    public String rejectRequest(@PathVariable Long id) {
        BorrowRequest request = borrowRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found"));

        request.setStatus(STATUS_REJECTED);
        borrowRequestRepository.save(request);
        return "Rejected!";
    }

    private User findUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
