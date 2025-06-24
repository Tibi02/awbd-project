package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> findAvailableHotels(LocalDate checkInDate, LocalDate checkOutDate) {
        return hotelRepository.findAvailableHotels(checkInDate, checkOutDate);
    }

    public List<Hotel> getAvailableHotels(String checkInDateStr, String checkOutDateStr) {
        LocalDate checkInDate = LocalDate.parse(checkInDateStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);

        // Now call the real method
        return hotelRepository.findAvailableHotels(checkInDate, checkOutDate);
    }

    public Hotel getHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId).orElse(null);
    }

    // Save a new hotel (used for adding hotels)
    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel); // Save the hotel to the database
    }

    // Delete a hotel (used for deleting hotels)
    public void deleteHotel(Long hotelId) {
        hotelRepository.deleteById(hotelId); // Delete the hotel from the database
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll(); // This assumes you have a method to fetch all hotels
    }
}
