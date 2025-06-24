package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.repositories.HotelRepository;
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
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    @Test
    void testFindAvailableHotels() {
        LocalDate checkInDate = LocalDate.of(2025, 1, 1);
        LocalDate checkOutDate = LocalDate.of(2025, 1, 10);

        Hotel hotel1 = new Hotel();
        hotel1.setId(1L);
        hotel1.setName("Hotel Sunshine");
        hotel1.setLocation("City A");
        hotel1.setRating(4);

        Hotel hotel2 = new Hotel();
        hotel2.setId(2L);
        hotel2.setName("Hotel Paradise");
        hotel2.setLocation("City B");
        hotel2.setRating(5);

        when(hotelRepository.findAvailableHotels(checkInDate, checkOutDate)).thenReturn(List.of(hotel1, hotel2));

        List<Hotel> availableHotels = hotelService.findAvailableHotels(checkInDate, checkOutDate);

        assertNotNull(availableHotels, "The list of available hotels should not be null");
        assertEquals(2, availableHotels.size(), "There should be 2 available hotels");
        assertEquals("Hotel Sunshine", availableHotels.get(0).getName());
        assertEquals("Hotel Paradise", availableHotels.get(1).getName());
        verify(hotelRepository, times(1)).findAvailableHotels(checkInDate, checkOutDate);
    }

    @Test
    void testGetHotelById_Success() {
        Long hotelId = 1L;

        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setName("Hotel Sunshine");
        hotel.setLocation("City A");
        hotel.setRating(4);

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));

        Hotel foundHotel = hotelService.getHotelById(hotelId);

        assertNotNull(foundHotel, "The hotel should not be null");
        assertEquals("Hotel Sunshine", foundHotel.getName());
        verify(hotelRepository, times(1)).findById(hotelId);
    }

    @Test
    void testGetHotelById_NotFound() {
        Long hotelId = 1L;

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        Hotel foundHotel = hotelService.getHotelById(hotelId);

        assertNull(foundHotel, "The hotel should be null if not found");
        verify(hotelRepository, times(1)).findById(hotelId);
    }

    @Test
    void testSaveHotel() {
        Hotel hotel = new Hotel();
        hotel.setName("Hotel Sunshine");
        hotel.setLocation("City A");
        hotel.setRating(4);

        when(hotelRepository.save(hotel)).thenReturn(hotel);

        Hotel savedHotel = hotelService.saveHotel(hotel);

        assertNotNull(savedHotel, "The saved hotel should not be null");
        assertEquals("Hotel Sunshine", savedHotel.getName());
        verify(hotelRepository, times(1)).save(hotel);
    }

    @Test
    void testDeleteHotel() {
        Long hotelId = 1L;

        doNothing().when(hotelRepository).deleteById(hotelId);

        hotelService.deleteHotel(hotelId);

        verify(hotelRepository, times(1)).deleteById(hotelId);
    }

    @Test
    void testGetAllHotels() {
        Hotel hotel1 = new Hotel();
        hotel1.setId(1L);
        hotel1.setName("Hotel Sunshine");
        hotel1.setLocation("City A");
        hotel1.setRating(4);

        Hotel hotel2 = new Hotel();
        hotel2.setId(2L);
        hotel2.setName("Hotel Paradise");
        hotel2.setLocation("City B");
        hotel2.setRating(5);

        when(hotelRepository.findAll()).thenReturn(List.of(hotel1, hotel2));

        List<Hotel> hotels = hotelService.getAllHotels();

        assertNotNull(hotels, "The list of hotels should not be null");
        assertEquals(2, hotels.size(), "There should be 2 hotels");
        assertEquals("Hotel Sunshine", hotels.get(0).getName());
        assertEquals("Hotel Paradise", hotels.get(1).getName());
        verify(hotelRepository, times(1)).findAll();
    }
}
