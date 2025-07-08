package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Payment;
import com.example.HotelManagement.models.PaymentMethod;
import com.example.HotelManagement.repositories.BookingRepository;
import com.example.HotelManagement.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public void processPayment(Long bookingId, String paymentMethod) {
        logger.info("Starting payment process for booking ID: {}", bookingId);

        if (bookingId == null) {
            logger.error("Booking ID is null. Cannot proceed with payment.");
            throw new IllegalArgumentException("Booking ID is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking with ID {} not found.", bookingId);
                    return new RuntimeException("Booking not found");
                });

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getRoom().getPricePerNight());
        payment.setPaymentDate(LocalDateTime.now());

        try {
            payment.setPaymentMethod(PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid payment method received: {}", paymentMethod);
            throw new RuntimeException("Invalid payment method: " + paymentMethod);
        }

        paymentRepository.save(payment);
        logger.info("Payment processed successfully for booking ID {} using method {}", bookingId, paymentMethod);
    }
}
