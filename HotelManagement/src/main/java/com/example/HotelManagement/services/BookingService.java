package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.repositories.BookingRepository;
import com.example.HotelManagement.repositories.HotelRepository;
import com.example.HotelManagement.repositories.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public void bookRoom(Long roomId, Long hotelId, String checkInDate, String checkOutDate) {
        logger.info("Attempting to book room with ID: {}, at hotel ID: {}, from {} to {}",
                roomId, hotelId, checkInDate, checkOutDate);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> {
                    logger.error("Room with ID {} not found", roomId);
                    return new IllegalArgumentException("Room not found");
                });

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> {
                    logger.error("Hotel with ID {} not found", hotelId);
                    return new IllegalArgumentException("Hotel not found");
                });

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setHotel(hotel);
        booking.setCheckInDate(LocalDate.parse(checkInDate));
        booking.setCheckOutDate(LocalDate.parse(checkOutDate));

        bookingRepository.save(booking);
        logger.info("Booking saved successfully for room ID: {}", roomId);
    }

    public Booking saveBooking(Booking booking) {
        if (booking.getHotel() == null) {
            logger.error("Attempted to save booking with null hotel");
            throw new IllegalArgumentException("Hotel cannot be null when saving a booking.");
        }
        Booking saved = bookingRepository.save(booking);
        logger.info("Saved booking with ID: {}", saved.getId());
        return saved;
    }

    public Booking getBookingById(Long id) {
        logger.info("Fetching booking with ID: {}", id);
        return bookingRepository.findById(id).orElse(null);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        logger.info("Fetching bookings for user ID: {}", userId);
        return bookingRepository.findByUserId(userId);
    }

    public void deleteBooking(Long bookingId) {
        logger.warn("Deleting booking with ID: {}", bookingId);
        bookingRepository.deleteById(bookingId);
    }
}
