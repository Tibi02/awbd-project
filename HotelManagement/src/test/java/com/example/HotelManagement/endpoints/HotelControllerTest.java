package com.example.HotelManagement.endpoints;

import com.example.HotelManagement.controllers.HotelController;
import com.example.HotelManagement.dto.HotelRoomDTO;
import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.services.HotelService;
import com.example.HotelManagement.services.HotelServiceEntityService;
import com.example.HotelManagement.services.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser; // Import for security
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // Import for CSRF
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Specify roles as needed
public class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private HotelService hotelService;

    @MockBean
    private HotelServiceEntityService hotelServiceEntityService;

    private Hotel hotel;
    private Room room;

    @BeforeEach
    public void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Hotel Test");

        room = new Room();
        room.setId(1L);
        room.setHotel(hotel);
        room.setPricePerNight(100.0);
    }

    @Test
    public void testShowRooms() throws Exception {
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(3);

        when(roomService.getAvailableRooms(1L, checkInDate, checkOutDate))
                .thenReturn(Collections.singletonList(room));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/show-rooms")
                        .param("hotelId", "1")
                        .param("checkInDate", checkInDate.toString())
                        .param("checkOutDate", checkOutDate.toString())
                        .with(csrf())) // GET requests generally don't require CSRF, but adding for consistency
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attributeExists("checkInDate"))
                .andExpect(model().attributeExists("checkOutDate"));

        verify(roomService, times(1)).getAvailableRooms(1L, checkInDate, checkOutDate);
    }

    @Test
    public void testSearchAvailableHotels() throws Exception {
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = checkInDate.plusDays(3);

        when(hotelService.findAvailableHotels(checkInDate, checkOutDate))
                .thenReturn(Collections.singletonList(hotel));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/hotel-search-available-hotels")
                        .param("checkInDate", checkInDate.toString())
                        .param("checkOutDate", checkOutDate.toString())
                        .with(csrf())) // GET requests generally don't require CSRF, but adding for consistency
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotels"))
                .andExpect(model().attributeExists("checkInDate"))
                .andExpect(model().attributeExists("checkOutDate"));

        verify(hotelService, times(1)).findAvailableHotels(checkInDate, checkOutDate);
    }

    @Test
    public void testShowHotelDetails() throws Exception {
        when(hotelService.getHotelById(1L)).thenReturn(hotel);
        when(hotelServiceEntityService.getServicesByHotelId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/hotel-details")
                        .param("hotelId", "1")
                        .with(csrf())) // GET requests generally don't require CSRF, but adding for consistency
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotel"))
                .andExpect(model().attributeExists("services"));

        verify(hotelService, times(1)).getHotelById(1L);
        verify(hotelServiceEntityService, times(1)).getServicesByHotelId(1L);
    }

    @Test
    public void testAddHotel() throws Exception {
        HotelRoomDTO hotelRoomDTO = new HotelRoomDTO();
        hotelRoomDTO.setHotel(hotel);
        hotelRoomDTO.setRoom(room);

        // Correctly stub non-void methods
        when(hotelService.saveHotel(any(Hotel.class))).thenReturn(hotel);
        when(roomService.saveRoom(any(Room.class))).thenReturn(room);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/add-hotel")
                        .flashAttr("hotelRoomDTO", hotelRoomDTO)
                        .with(csrf())) // Include CSRF token
                .andExpect(status().is3xxRedirection());

        verify(hotelService, times(1)).saveHotel(any(Hotel.class));
        verify(roomService, times(1)).saveRoom(any(Room.class));
    }

    @Test
    public void testDeleteHotel() throws Exception {
        doNothing().when(hotelService).deleteHotel(1L);
        when(hotelService.getAllHotels()).thenReturn(Collections.singletonList(hotel));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/delete-hotel")
                        .param("hotelId", "1")
                        .with(csrf())) // Include CSRF token
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("hotels"))
                .andExpect(model().attributeExists("successMessage"));

        verify(hotelService, times(1)).deleteHotel(1L);
        verify(hotelService, times(1)).getAllHotels();
    }
}
