package com.example.HotelManagement.repositories;

import com.example.HotelManagement.models.HotelServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelServiceEntityRepository
        extends JpaRepository<HotelServiceEntity, Long> {

    List<HotelServiceEntity> findByHotelId(Long hotelId);
}


