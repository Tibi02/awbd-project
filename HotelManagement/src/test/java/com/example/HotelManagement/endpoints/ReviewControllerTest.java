package com.example.HotelManagement.endpoints;

import com.example.HotelManagement.config.TestSecurityConfig;
import com.example.HotelManagement.controllers.ReviewController;
import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Review;
import com.example.HotelManagement.services.BookingService;
import com.example.HotelManagement.services.ReviewService;
import com.example.HotelManagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(TestSecurityConfig.class) // Import the test security configuration
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    private Booking booking;
    private Review review;

    @BeforeEach
    public void setUp() {
        // Initialize mock Booking and Review objects
        booking = new Booking();
        booking.setId(1L);
        booking.setUser(Mockito.mock(com.example.HotelManagement.models.User.class));
        booking.getUser().setId(1L);
        booking.setHotel(Mockito.mock(com.example.HotelManagement.models.Hotel.class));
        booking.getHotel().setId(1L);

        review = new Review();
        review.setUserId(1L);
        review.setHotelId(1L);
        review.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser // Mock a user for authentication
    public void testShowRatingForm() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/rate-booking")
                        .param("bookingId", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("review"))
                .andExpect(model().attributeExists("booking"));

        verify(bookingService, times(1)).getBookingById(1L);
    }

    @Test
    @WithMockUser // Mock a user for authentication
    public void testSubmitReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/submit-review")
                        .param("userId", "1")
                        .param("hotelId", "1")
                        .param("createdAt", LocalDateTime.now().toString()))
                .andExpect(status().is3xxRedirection());

        verify(reviewService, times(1)).saveReview(Mockito.any(Review.class));
    }
}
