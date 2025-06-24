package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Booking;
import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.repositories.BookingRepository;
import com.example.HotelManagement.repositories.HotelRepository;
import com.example.HotelManagement.repositories.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void testBookRoom_Success() {
        Long roomId = 1L;
        Long hotelId = 1L;
        String checkInDate = "2025-01-01";
        String checkOutDate = "2025-01-10";

        Room mockRoom = new Room();
        mockRoom.setId(roomId);
        mockRoom.setRoomNumber("101");

        Hotel mockHotel = new Hotel();
        mockHotel.setId(hotelId);
        mockHotel.setName("Hotel Sunshine");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(mockHotel));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookingService.bookRoom(roomId, hotelId, checkInDate, checkOutDate);

        verify(roomRepository, times(1)).findById(roomId);
        verify(hotelRepository, times(1)).findById(hotelId);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testBookRoom_RoomNotFound() {
        Long roomId = 1L;
        Long hotelId = 1L;
        String checkInDate = "2025-01-01";
        String checkOutDate = "2025-01-10";

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookingService.bookRoom(roomId, hotelId, checkInDate, checkOutDate));

        verify(roomRepository, times(1)).findById(roomId);
        verify(hotelRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testSaveBooking_Success() {
        Booking booking = new Booking();
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        booking.setHotel(hotel);

        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking savedBooking = bookingService.saveBooking(booking);

        assertNotNull(savedBooking, "The saved booking should not be null");
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void testSaveBooking_HotelNull() {
        Booking booking = new Booking();

        assertThrows(IllegalArgumentException.class, () -> bookingService.saveBooking(booking));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testGetBookingById_Success() {
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking foundBooking = bookingService.getBookingById(bookingId);

        assertNotNull(foundBooking, "The booking should not be null");
        assertEquals(bookingId, foundBooking.getId());
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testGetBookingById_NotFound() {
        Long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        Booking foundBooking = bookingService.getBookingById(bookingId);

        assertNull(foundBooking, "The booking should be null if not found");
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testGetBookingsByUserId() {
        Long userId = 1L;

        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(2L);

        when(bookingRepository.findByUserId(userId)).thenReturn(List.of(booking1, booking2));

        List<Booking> bookings = bookingService.getBookingsByUserId(userId);

        assertNotNull(bookings, "The list of bookings should not be null");
        assertEquals(2, bookings.size(), "There should be 2 bookings in the list");
        verify(bookingRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testDeleteBooking() {
        Long bookingId = 1L;

        doNothing().when(bookingRepository).deleteById(bookingId);

        bookingService.deleteBooking(bookingId);

        verify(bookingRepository, times(1)).deleteById(bookingId);
    }
}
