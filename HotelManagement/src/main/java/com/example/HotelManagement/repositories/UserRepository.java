package com.example.HotelManagement.repositories;

import com.example.HotelManagement.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query method to find user by email
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);

}
