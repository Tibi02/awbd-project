package com.example.HotelManagement.dto;

import com.example.HotelManagement.models.Hotel;
import com.example.HotelManagement.models.Room;

public class HotelRoomDTO {
    private Hotel hotel;
    private Room room;

    public HotelRoomDTO() {
        this.hotel = new Hotel();
        this.room = new Room();
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
