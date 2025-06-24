package com.example.HotelManagement.endpoints;

import com.example.HotelManagement.controllers.BookingController;
import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.models.User;
import com.example.HotelManagement.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @MockBean
    private RoomService roomService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private UserService userService;

    private User user;
    private Hotel hotel;
    private Room room;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Initialize test data with future dates
        user = new User();
        user.setId(1L);
        // Initialize other user fields if necessary

        hotel = new Hotel();
        hotel.setId(1L);
        // Initialize other hotel fields if necessary

        room = new Room();
        room.setId(1L);
        room.setHotel(hotel);
        // Initialize other room fields if necessary

        booking = new Booking();
        booking.setId(1L);
        booking.setRoom(room);
        booking.setHotel(hotel);
        booking.setUser(user);
        booking.setCheckInDate(LocalDate.of(2025, 2, 1)); // Future date
        booking.setCheckOutDate(LocalDate.of(2025, 2, 2)); // Future date
        // Initialize other booking fields if necessary
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testSearchAvailableHotels() throws Exception {
        when(hotelService.findAvailableHotels(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(hotel));

        mockMvc.perform(get("/search-available-hotels")
                        .param("checkInDate", "2025-01-01")
                        .param("checkOutDate", "2025-01-02"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotels"))
                .andExpect(view().name("available-hotels"));

        verify(hotelService, times(1)).findAvailableHotels(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testBookRoom() throws Exception {
        when(roomService.getRoomById(anyLong())).thenReturn(room);
        when(userService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/book-room")
                        .param("roomId", "1")
                        .param("checkInDate", "2025-01-01")
                        .param("checkOutDate", "2025-01-02"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("booking"))
                .andExpect(view().name("payment"));

        verify(roomService, times(1)).getRoomById(1L);
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testProcessBookingPayment() throws Exception {
        when(roomService.getRoomById(anyLong())).thenReturn(room);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(bookingService.saveBooking(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/process-booking-payment")
                        .param("roomId", "1")
                        .param("userId", "1")
                        .param("checkInDate", "2025-01-01")
                        .param("checkOutDate", "2025-01-02")
                        .param("paymentMethod", "CREDIT_CARD")
                        .with(csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("booking"))
                .andExpect(view().name("payment-success"));

        verify(paymentService, times(1)).processPayment(1L, "CREDIT_CARD");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testShowMyBookings() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(bookingService.getBookingsByUserId(anyLong())).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(get("/my-bookings"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bookings"))
                .andExpect(view().name("my-bookings"));

        verify(bookingService, times(1)).getBookingsByUserId(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteBooking() throws Exception {
        when(bookingService.getBookingById(anyLong())).thenReturn(booking);
        when(userService.getCurrentUser()).thenReturn(user);
        doNothing().when(bookingService).deleteBooking(anyLong());

        mockMvc.perform(post("/delete-booking")
                        .param("bookingId", "1")
                        .with(csrf())) // Include CSRF token
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-bookings"));

        verify(bookingService, times(1)).deleteBooking(1L);
    }
}
