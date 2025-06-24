package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Review;
import com.example.HotelManagement.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void testSaveReview() {
        Review review = new Review();
        review.setUserId(1L);
        review.setHotelId(1L);
        review.setRating(5);
        review.setComment("Excellent stay!");
        review.setCreatedAt(LocalDateTime.now());

        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review savedReview = reviewService.saveReview(review);

        assertNotNull(savedReview, "The saved review should not be null");
        assertEquals(5, savedReview.getRating(), "The rating should match");
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void testGetReviewsByUserId() {
        Long userId = 1L;

        Review review1 = new Review();
        review1.setUserId(userId);
        review1.setHotelId(1L);
        review1.setRating(5);
        review1.setComment("Great experience!");

        Review review2 = new Review();
        review2.setUserId(userId);
        review2.setHotelId(2L);
        review2.setRating(4);
        review2.setComment("Very good service.");

        when(reviewRepository.findByUserId(userId)).thenReturn(List.of(review1, review2));

        List<Review> userReviews = reviewService.getReviewsByUserId(userId);

        assertNotNull(userReviews, "The list of reviews should not be null");
        assertEquals(2, userReviews.size(), "There should be 2 reviews in the list");
        assertEquals("Great experience!", userReviews.get(0).getComment());
        assertEquals("Very good service.", userReviews.get(1).getComment());
        verify(reviewRepository, times(1)).findByUserId(userId);
    }
}
