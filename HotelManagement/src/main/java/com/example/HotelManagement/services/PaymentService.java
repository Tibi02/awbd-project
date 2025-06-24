package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Payment;
import com.example.HotelManagement.models.PaymentMethod;
import com.example.HotelManagement.repositories.BookingRepository;
import com.example.HotelManagement.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public void processPayment(Long bookingId, String paymentMethod) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getRoom().getPricePerNight());  // Calculate payment amount
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(PaymentMethod.valueOf(paymentMethod.toUpperCase()));

        paymentRepository.save(payment);

        System.out.println("Processing payment for booking ID: " + bookingId + " with method: " + paymentMethod);

    }

}
