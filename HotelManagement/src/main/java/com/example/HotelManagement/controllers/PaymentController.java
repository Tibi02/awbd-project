package com.example.HotelManagement.controllers;

import com.example.HotelManagement.exceptions.PaymentProcessingException;
import com.example.HotelManagement.exceptions.RoomNotFoundException;
import org.springframework.ui.Model;
import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Payment;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.services.BookingService;
import com.example.HotelManagement.services.PaymentService;
import com.example.HotelManagement.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    @PostMapping("/process-payment")
    public String processPayment(@RequestParam Long roomId,
                                 @RequestParam String checkInDate,
                                 @RequestParam String checkOutDate,
                                 @RequestParam String paymentMethod) {
        try {
            // Fetch room details
            Room room = roomService.getRoomById(roomId);
            if (room == null) {
                throw new RoomNotFoundException("Room with ID " + roomId + " not found.");
            }

            // Create and save booking
            Booking booking = new Booking();
            booking.setRoom(room);
            booking.setHotel(room.getHotel());
            booking.setCheckInDate(LocalDate.parse(checkInDate));
            booking.setCheckOutDate(LocalDate.parse(checkOutDate));
            bookingService.saveBooking(booking);

            // Process payment
            paymentService.processPayment(roomId, paymentMethod);

            // Redirect to success page
            return "redirect:/payment-success";
        } catch (RoomNotFoundException | PaymentProcessingException ex) {
            // Re-throw exceptions to be handled by GlobalExceptionHandler
            throw ex;
        } catch (Exception ex) {
            // Handle other unexpected exceptions
            throw new PaymentProcessingException("An unexpected error occurred.");
        }
    }
}
