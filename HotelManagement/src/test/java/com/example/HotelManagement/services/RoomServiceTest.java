package com.example.HotelManagement.services;

import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAvailableRooms() {
        Long hotelId = 1L;
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(2);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setType("Single");
        room.setPricePerNight(100.0);

        when(roomRepository.findAvailableRooms(hotelId, checkInDate, checkOutDate))
                .thenReturn(List.of(room));

        List<Room> availableRooms = roomService.getAvailableRooms(hotelId, checkInDate, checkOutDate);

        assertNotNull(availableRooms);
        assertEquals(1, availableRooms.size());
        assertEquals("101", availableRooms.get(0).getRoomNumber());
        verify(roomRepository, times(1)).findAvailableRooms(hotelId, checkInDate, checkOutDate);
    }

    @Test
    void testGetRoomById() {
        Long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);
        room.setRoomNumber("101");
        room.setType("Single");
        room.setPricePerNight(100.0);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Room fetchedRoom = roomService.getRoomById(roomId);

        assertNotNull(fetchedRoom);
        assertEquals(roomId, fetchedRoom.getId());
        verify(roomRepository, times(1)).findById(roomId);
    }

    @Test
    void testSaveRoom() {
        Room room = new Room();
        room.setRoomNumber("101");
        room.setType("Single");
        room.setPricePerNight(100.0);
        room.setHotel(new Hotel());

        when(roomRepository.save(room)).thenReturn(room);

        Room savedRoom = roomService.saveRoom(room);

        assertNotNull(savedRoom);
        assertEquals("101", savedRoom.getRoomNumber());
        verify(roomRepository, times(1)).save(room);
    }
}