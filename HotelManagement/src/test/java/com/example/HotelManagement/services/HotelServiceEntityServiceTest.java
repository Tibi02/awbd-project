package com.example.HotelManagement.services;

import com.example.HotelManagement.models.HotelServiceEntity;
import com.example.HotelManagement.repositories.HotelServiceEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceEntityServiceTest {

    @Mock
    private HotelServiceEntityRepository repository;

    @InjectMocks
    private HotelServiceEntityService service;

    @Test
    void testGetServicesByHotelId_Success() {
        Long hotelId = 1L;

        HotelServiceEntity service1 = new HotelServiceEntity();
        service1.setId(1L);
        service1.setHotelId(hotelId);
        service1.setName("Room Cleaning");
        service1.setDescription("Daily room cleaning service");
        service1.setPrice(BigDecimal.valueOf(50.00));

        HotelServiceEntity service2 = new HotelServiceEntity();
        service2.setId(2L);
        service2.setHotelId(hotelId);
        service2.setName("Spa");
        service2.setDescription("Access to the spa facilities");
        service2.setPrice(BigDecimal.valueOf(100.00));

        when(repository.findByHotelId(hotelId)).thenReturn(List.of(service1, service2));

        List<HotelServiceEntity> services = service.getServicesByHotelId(hotelId);

        assertNotNull(services, "The services list should not be null");
        assertEquals(2, services.size(), "The services list should contain 2 items");
        assertEquals("Room Cleaning", services.get(0).getName());
        assertEquals("Spa", services.get(1).getName());
        verify(repository, times(1)).findByHotelId(hotelId);
    }

    @Test
    void testGetServicesByHotelId_NoServicesFound() {
        Long hotelId = 1L;

        when(repository.findByHotelId(hotelId)).thenReturn(List.of());

        List<HotelServiceEntity> services = service.getServicesByHotelId(hotelId);

        assertNotNull(services, "The services list should not be null");
        assertEquals(0, services.size(), "The services list should be empty");
        verify(repository, times(1)).findByHotelId(hotelId);
    }
}
