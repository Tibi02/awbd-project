package com.example.HotelManagement.services;

import com.example.HotelManagement.models.User;
import com.example.HotelManagement.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(User user) {
        logger.info("Saving user with email: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        logger.info("User saved successfully: {}", user.getEmail());
    }

    public boolean validateUser(String email, String password) {
        logger.info("Validating user with email: {}", email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        boolean isValid = optionalUser.map(user -> passwordEncoder.matches(password, user.getPassword())).orElse(false);
        if (isValid) {
            logger.info("User credentials are valid for: {}", email);
        } else {
            logger.warn("Invalid credentials for: {}", email);
        }
        return isValid;
    }

    public User getUserById(Long userId) {
        logger.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId).orElse(null);
    }

    public User getCurrentUser() {

        logger.warn("Returning mock user as current user (authentication not implemented)");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Default User");
        mockUser.setEmail("default@hotel.com");
        mockUser.setPassword("password");
        return mockUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

}
