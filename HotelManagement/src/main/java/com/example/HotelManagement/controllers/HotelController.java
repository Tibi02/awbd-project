package com.example.HotelManagement.controllers;

import com.example.HotelManagement.dto.HotelRoomDTO;
import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.HotelServiceEntity;
import com.example.HotelManagement.models.Room;
import com.example.HotelManagement.services.HotelService;
import com.example.HotelManagement.services.HotelServiceEntityService;
import com.example.HotelManagement.services.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HotelController {

    private final RoomService roomService;
    private final HotelService hotelService;
    private final HotelServiceEntityService hotelServiceEntityService;

    public HotelController(RoomService roomService,
                           HotelService hotelService,
                           HotelServiceEntityService hotelServiceEntityService) {
        this.roomService = roomService;
        this.hotelService = hotelService;
        this.hotelServiceEntityService = hotelServiceEntityService;
    }

    @GetMapping("/show-rooms")
    public String showRooms(@RequestParam Long hotelId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                            Model model) {
        List<Room> rooms = roomService.getAvailableRooms(hotelId, checkInDate, checkOutDate);
        model.addAttribute("rooms", rooms);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        return "hotel-rooms";
    }

    @GetMapping("/hotel-search-available-hotels")
    public String searchAvailableHotels(@RequestParam String checkInDate,
                                        @RequestParam String checkOutDate,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        @RequestParam(defaultValue = "name") String sortBy,
                                        Model model) {
        LocalDate inDate = LocalDate.parse(checkInDate);
        LocalDate outDate = LocalDate.parse(checkOutDate);

        Page<Hotel> hotelsPage = hotelService.findAvailableHotelsPaginated(inDate, outDate, PageRequest.of(page, size, Sort.by(sortBy)));

        model.addAttribute("hotelsPage", hotelsPage);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        return "available-hotels";
    }

    @GetMapping("/hotel-details")
    public String showHotelDetails(@RequestParam Long hotelId, Model model) {
        Hotel hotel = hotelService.getHotelById(hotelId);
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel not found with ID: " + hotelId);
        }
        List<HotelServiceEntity> services = hotelServiceEntityService.getServicesByHotelId(hotelId);
        model.addAttribute("hotel", hotel);
        model.addAttribute("services", services);
        return "hotel-details";
    }

    // Show add hotel form
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add-hotel")
    public String showAddHotelForm(Model model) {
        model.addAttribute("hotel", new Hotel());
        return "add-hotel";
    }

    // Handle adding hotel
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-hotel")
    public String addHotel(@ModelAttribute HotelRoomDTO hotelRoomDTO, Model model) {
        Hotel hotel = hotelRoomDTO.getHotel();
        Room room = hotelRoomDTO.getRoom();

        // Save the hotel first
        hotelService.saveHotel(hotel);

        // Link the room to the hotel
        room.setHotel(hotel);

        // Save the room
        roomService.saveRoom(room);

        // Redirect to check-in page
        return "redirect:/checkin-checkout";
    }

    // Show add room form
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add-room")
    public String showAddRoomForm(Model model) {
        // Fetch all available hotels
        List<Hotel> hotels = hotelService.getAllHotels();
        model.addAttribute("room", new Room());
        model.addAttribute("hotels", hotels); // Add the list of hotels to the model
        return "add-room";
    }

    // Handle adding room
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-room")
    public String addRoom(@ModelAttribute Room room, @RequestParam Long hotelId) {
        System.out.println("Room Price per Night: " + room.getPricePerNight()); // Debugging line
        room.setHotel(hotelService.getHotelById(hotelId));
        roomService.saveRoom(room);
        return "redirect:/checkin-checkout";
    }

    // Show delete hotel form
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete-hotel-form")
    public String showDeleteHotelForm(Model model) {
        List<Hotel> hotels = hotelService.getAllHotels();
        System.out.println("Number of hotels retrieved for deletion: " + hotels.size()); // Debugging line
        model.addAttribute("hotels", hotels);
        return "delete-hotel"; // Corresponds to delete-hotel.html
    }

    // Handle deleting hotel
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete-hotel")
    public String deleteHotel(@RequestParam Long hotelId, Model model) {
        try {
            hotelService.deleteHotel(hotelId);
            model.addAttribute("successMessage", "Hotel deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting hotel: " + e.getMessage());
        }
        // Reload the delete-hotel view with updated hotels
        List<Hotel> hotels = hotelService.getAllHotels();
        model.addAttribute("hotels", hotels);
        return "delete-hotel"; // Return to delete-hotel.html with messages
    }

    // Optional: Root mapping to show checkin-checkout (ensure no conflict)
    // Commented out to resolve mapping conflict if necessary
    /*
    @GetMapping("/")
    public String showDashboard(Model model) {
        List<Hotel> hotels = hotelService.getAllHotels();
        model.addAttribute("hotels", hotels);
        return "checkin-checkout";
    }
    */
}
