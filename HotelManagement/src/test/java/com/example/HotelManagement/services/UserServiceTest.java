package com.example.HotelManagement.services;

import com.example.HotelManagement.models.User;
import com.example.HotelManagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testValidateUser_ValidCredentials() {
        String email = "john.doe@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        boolean isValid = userService.validateUser(email, password);

        assertTrue(isValid);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testValidateUser_InvalidCredentials() {
        String email = "john.doe@example.com";
        String password = "wrongpassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword("password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        boolean isValid = userService.validateUser(email, password);

        assertFalse(isValid);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User fetchedUser = userService.getUserById(userId);

        assertNotNull(fetchedUser);
        assertEquals(userId, fetchedUser.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetCurrentUser() {
        User currentUser = userService.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals("Default User", currentUser.getName());
        assertEquals("default@hotel.com", currentUser.getEmail());
        assertEquals("password", currentUser.getPassword());
    }
}