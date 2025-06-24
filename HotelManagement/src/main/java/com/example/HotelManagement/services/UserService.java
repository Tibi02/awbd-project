package com.example.HotelManagement.services;

import com.example.HotelManagement.models.User;
import com.example.HotelManagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    public boolean validateUser(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(user -> user.getPassword().equals(password)).orElse(false);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // Updated to return a mock user if no authentication context is available
    public User getCurrentUser() {
        // Mock user creation for now
        User mockUser = new User();
        mockUser.setId(1L); // Mock user ID
        mockUser.setName("Default User");
        mockUser.setEmail("default@hotel.com");
        mockUser.setPassword("password");
        return mockUser;
    }
}
