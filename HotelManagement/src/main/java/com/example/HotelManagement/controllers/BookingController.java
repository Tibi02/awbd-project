package com.example.HotelManagement.controllers;

import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.User;
import com.example.HotelManagement.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public List<Hotel> searchAvailableHotels(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        return hotelService.findAvailableHotels(checkInDate, checkOutDate);
    }

    @PostMapping("/book")
    public Booking bookRoom(
            @RequestParam Long roomId,
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            throw new RuntimeException("Room with ID " + roomId + " not found.");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User with ID " + userId + " not found.");
        }

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setHotel(room.getHotel());
        booking.setUser(user);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);

        return bookingService.saveBooking(booking);
    }

    @PostMapping("/process-payment")
    public String processBookingPayment(
            @RequestParam Long bookingId,
            @RequestParam String paymentMethod) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking with ID " + bookingId + " not found.");
        }

        paymentService.processPayment(booking.getId(), paymentMethod);
        return "Payment successful for booking ID: " + bookingId;
    }

    @GetMapping("/my-bookings")
    public List<Booking> showMyBookings() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No current user found.");
        }
        return bookingService.getBookingsByUserId(currentUser.getId());
    }

    @DeleteMapping("/delete/{bookingId}")
    public String deleteBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found.");
        }

        User currentUser = userService.getCurrentUser();
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Unauthorized delete attempt.");
        }

        if (booking.getCheckInDate().isAfter(LocalDate.now())) {
            bookingService.deleteBooking(bookingId);
            return "Booking deleted successfully.";
        } else {
            throw new IllegalStateException("Can't delete after check-in date has passed.");
        }
    }
}
