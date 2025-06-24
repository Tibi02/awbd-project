package com.example.HotelManagement.repositories;

import com.example.HotelManagement.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.id NOT IN (" +
            "SELECT b.room.id FROM Booking b WHERE " +
            "b.hotel.id = :hotelId AND " +
            "(:checkInDate BETWEEN b.checkInDate AND b.checkOutDate OR " +
            ":checkOutDate BETWEEN b.checkInDate AND b.checkOutDate))")
    List<Room> findAvailableRooms(@Param("hotelId") Long hotelId,
                                  @Param("checkInDate") LocalDate checkInDate,
                                  @Param("checkOutDate") LocalDate checkOutDate);
}
