package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Payment;
import com.example.HotelManagement.models.PaymentMethod;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.repositories.BookingRepository;
import com.example.HotelManagement.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPayment_Success() {
        Long bookingId = 1L;
        String paymentMethod = "CREDIT_CARD";

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setRoom(new Room());
        booking.getRoom().setPricePerNight(150.0);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        doAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            assertNotNull(savedPayment);
            assertEquals(booking, savedPayment.getBooking());
            assertEquals(150.0, savedPayment.getAmount());
            assertEquals(PaymentMethod.CREDIT_CARD, savedPayment.getPaymentMethod());
            assertNotNull(savedPayment.getPaymentDate());
            return null;
        }).when(paymentRepository).save(any(Payment.class));

        paymentService.processPayment(bookingId, paymentMethod);

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_BookingNotFound() {
        Long bookingId = 1L;
        String paymentMethod = "CREDIT_CARD";

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                paymentService.processPayment(bookingId, paymentMethod));

        assertEquals("Booking not found", exception.getMessage());
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_InvalidPaymentMethod() {
        Long bookingId = 1L;
        String paymentMethod = "INVALID_METHOD";

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setRoom(new Room());
        booking.getRoom().setPricePerNight(150.0);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                paymentService.processPayment(bookingId, paymentMethod));

        assertTrue(exception.getMessage().contains("No enum constant"));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
