package com.example.HotelManagement.repositories;

import com.example.HotelManagement.models.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    @Query("SELECT h FROM Hotel h WHERE h.id NOT IN (" +
            "SELECT b.room.hotel.id FROM Booking b WHERE " +
            "b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate)")
    List<Hotel> findAvailableHotels(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);
}


