package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.repositories.HotelRepository;
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HotelService {

    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);

    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> findAvailableHotels(LocalDate checkInDate, LocalDate checkOutDate) {
        logger.info("Finding available hotels between {} and {}", checkInDate, checkOutDate);
        return hotelRepository.findAvailableHotels(checkInDate, checkOutDate);
    }

    public List<Hotel> getAvailableHotels(String checkInDateStr, String checkOutDateStr) {
        logger.info("Parsing check-in and check-out dates from strings: {}, {}", checkInDateStr, checkOutDateStr);
        LocalDate checkInDate = LocalDate.parse(checkInDateStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);

        return findAvailableHotels(checkInDate, checkOutDate);
    }

    public Hotel getHotelById(Long hotelId) {
        logger.info("Fetching hotel with ID: {}", hotelId);
        return hotelRepository.findById(hotelId).orElse(null);
    }

    public Hotel saveHotel(Hotel hotel) {
        logger.info("Saving hotel: {}", hotel.getName());
        return hotelRepository.save(hotel);
    }

    public void deleteHotel(Long hotelId) {
        logger.warn("Deleting hotel with ID: {}", hotelId);
        hotelRepository.deleteById(hotelId);
    }

    public List<Hotel> getAllHotels() {
        logger.info("Fetching all hotels");
        return hotelRepository.findAll();
    }

    public Page<Hotel> findAvailableHotelsPaginated(LocalDate checkInDate, LocalDate checkOutDate, Pageable pageable) {
        logger.info("Paginated query for hotels between {} and {}", checkInDate, checkOutDate);
        return hotelRepository.findAvailableHotelsPaginated(checkInDate, checkOutDate, pageable);
    }
}
