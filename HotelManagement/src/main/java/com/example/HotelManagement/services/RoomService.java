package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAvailableRooms(Long hotelId, LocalDate checkInDate, LocalDate checkOutDate) {
        return roomRepository.findAvailableRooms(hotelId, checkInDate, checkOutDate);
    }

    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    // Save a new room (used for adding rooms)
    public Room saveRoom(Room room) {
        return roomRepository.save(room); // Save the room to the database
    }

}
