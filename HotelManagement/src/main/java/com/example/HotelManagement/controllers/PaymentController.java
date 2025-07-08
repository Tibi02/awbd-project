package com.example.HotelManagement.controllers;

import com.example.HotelManagement.exceptions.PaymentProcessingException;
import com.example.HotelManagement.exceptions.RoomNotFoundException;
import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.models.User;
import com.example.HotelManagement.services.BookingService;
import com.example.HotelManagement.services.PaymentService;
import com.example.HotelManagement.services.RoomService;
import com.example.HotelManagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @PostMapping("/handle-payment")
    public String processPayment(
            @RequestParam Long roomId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam String paymentMethod) {

        try {
            Room room = roomService.getRoomById(roomId);
            if (room == null) {
                throw new RoomNotFoundException("Room with ID " + roomId + " not found.");
            }

            if (room.getHotel() == null) {
                throw new RuntimeException("Room does not have a hotel assigned!");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("No authenticated user.");
            }

            Booking booking = new Booking();
            booking.setRoom(room);
            booking.setHotel(room.getHotel());
            booking.setUser(user);
            booking.setCheckInDate(LocalDate.parse(checkInDate));
            booking.setCheckOutDate(LocalDate.parse(checkOutDate));

            Booking savedBooking = bookingService.saveBooking(booking);

            paymentService.processPayment(savedBooking.getId(), paymentMethod);

            return "redirect:/payment-success";

        } catch (RoomNotFoundException | PaymentProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new PaymentProcessingException("An unexpected error occurred during payment.");
        }
    }


    @GetMapping("/payment-success")
    public String showPaymentSuccessPage(Model model) {
        model.addAttribute("message", "Thank you! Your payment was successful.");
        return "payment-success";
    }
}
