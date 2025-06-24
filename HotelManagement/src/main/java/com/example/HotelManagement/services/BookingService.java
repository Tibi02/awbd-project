package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.repositories.BookingRepository;
import com.example.HotelManagement.repositories.HotelRepository;
import com.example.HotelManagement.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public void bookRoom(Long roomId, Long hotelId, String checkInDate, String checkOutDate) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));


        Hotel hotel = hotelRepository.findById(hotelId)  // Fetch the Hotel entity
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setHotel(hotel);  // Set the Hotel entity
        booking.setCheckInDate(LocalDate.parse(checkInDate));
        booking.setCheckOutDate(LocalDate.parse(checkOutDate));

        bookingRepository.save(booking);
    }



    public Booking saveBooking(Booking booking) {
        if (booking.getHotel() == null) {
            throw new IllegalArgumentException("Hotel cannot be null when saving a booking.");
        }
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}