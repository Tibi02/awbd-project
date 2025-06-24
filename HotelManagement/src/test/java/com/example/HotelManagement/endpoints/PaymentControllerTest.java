package com.example.HotelManagement.endpoints;

import com.example.HotelManagement.exceptions.GlobalExceptionHandler; // Import the exception handler
import com.example.HotelManagement.controllers.PaymentController;
import com.example.HotelManagement.exceptions.PaymentProcessingException;
import com.example.HotelManagement.exceptions.RoomNotFoundException;
import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.services.BookingService;
import com.example.HotelManagement.services.PaymentService;
import com.example.HotelManagement.services.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable Spring Security filters
@Import({GlobalExceptionHandler.class}) // Import the GlobalExceptionHandler
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private RoomService roomService;

    private Room room;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        // Mock Room
        room = new Room();
        room.setId(1L);
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        room.setHotel(hotel);
        room.setPricePerNight(100.0);

        // Mock Booking
        booking = new Booking();
        booking.setId(1L);
        booking.setRoom(room);
        booking.setHotel(room.getHotel());
        booking.setCheckInDate(LocalDate.of(2025, 1, 10));
        booking.setCheckOutDate(LocalDate.of(2025, 1, 15));
    }

    @Test
    public void testProcessPayment() throws Exception {
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(bookingService.saveBooking(any(Booking.class))).thenReturn(booking);
        doNothing().when(paymentService).processPayment(1L, "CREDIT_CARD");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/process-payment")
                        .param("roomId", "1")
                        .param("checkInDate", "2025-01-10")
                        .param("checkOutDate", "2025-01-15")
                        .param("paymentMethod", "CREDIT_CARD")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(roomService, times(1)).getRoomById(1L);
        verify(bookingService, times(1)).saveBooking(any(Booking.class));
        verify(paymentService, times(1)).processPayment(1L, "CREDIT_CARD");
    }

    @Test
    public void testProcessPaymentRoomNotFound() throws Exception {
        when(roomService.getRoomById(1L)).thenThrow(new RoomNotFoundException("Room with ID 1 not found."));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/process-payment")
                        .param("roomId", "1")
                        .param("checkInDate", "2025-01-10")
                        .param("checkOutDate", "2025-01-15")
                        .param("paymentMethod", "CREDIT_CARD")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Room with ID 1 not found.")); // Match plain text response

        verify(roomService, times(1)).getRoomById(1L);
        verify(bookingService, never()).saveBooking(any(Booking.class));
        verify(paymentService, never()).processPayment(anyLong(), anyString());
    }

    @Test
    public void testProcessPaymentThrowsException() throws Exception {
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(bookingService.saveBooking(any(Booking.class))).thenReturn(booking);

        doThrow(new PaymentProcessingException("Payment processing failed"))
                .when(paymentService).processPayment(1L, "CREDIT_CARD");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/process-payment")
                        .param("roomId", "1")
                        .param("checkInDate", "2025-01-10")
                        .param("checkOutDate", "2025-01-15")
                        .param("paymentMethod", "CREDIT_CARD")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Payment processing failed")); // Match plain text response

        verify(roomService, times(1)).getRoomById(1L);
        verify(bookingService, times(1)).saveBooking(any(Booking.class));
        verify(paymentService, times(1)).processPayment(1L, "CREDIT_CARD");
    }



}
