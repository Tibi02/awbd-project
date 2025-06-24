package com.example.HotelManagement.controllers;

import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Review;
import com.example.HotelManagement.services.BookingService;
import com.example.HotelManagement.services.ReviewService;
import com.example.HotelManagement.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class ReviewController {

    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(BookingService bookingService,
                            ReviewService reviewService,
                            UserService userService) {
        this.bookingService = bookingService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // 1) Show a rating form for a specific booking
    @GetMapping("/rate-booking")
    public String showRatingForm(@RequestParam Long bookingId,
                                 Model model) {
        // Find the booking
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Invalid booking ID: " + bookingId);
        }

        // Create an empty Review object to capture form input
        Review review = new Review();
        // Pre-fill userId, hotelId from booking, so the user doesn't have to
        review.setUserId(booking.getUser().getId());    // Or however you store the user
        review.setHotelId(booking.getHotel().getId());

        model.addAttribute("review", review);
        model.addAttribute("booking", booking);
        return "review-form";  // We'll create review-form.html next
    }

    // 2) Handle the form submission
    @PostMapping("/submit-review")
    public String submitReview(@ModelAttribute Review review) {
        // Set the created date/time
        review.setCreatedAt(LocalDateTime.now());
        // Save the review
        reviewService.saveReview(review);

        // After saving, redirect the user back to "my-bookings" or somewhere else
        return "redirect:/my-bookings";
    }
}
