package com.example.HotelManagement.repositories;

import com.example.HotelManagement.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // e.g. find reviews for a specific user
    List<Review> findByUserId(Long userId);

    // or for a specific hotel
    List<Review> findByHotelId(Long hotelId);
}

