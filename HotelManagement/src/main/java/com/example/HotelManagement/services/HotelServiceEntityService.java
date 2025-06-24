package com.example.HotelManagement.services;

import com.example.HotelManagement.models.HotelServiceEntity;
import com.example.HotelManagement.repositories.HotelServiceEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceEntityService {

    private final HotelServiceEntityRepository repository;

    @Autowired
    public HotelServiceEntityService(HotelServiceEntityRepository repository) {
        this.repository = repository;
    }

    public List<HotelServiceEntity> getServicesByHotelId(Long hotelId) {
        return repository.findByHotelId(hotelId);
    }
}
