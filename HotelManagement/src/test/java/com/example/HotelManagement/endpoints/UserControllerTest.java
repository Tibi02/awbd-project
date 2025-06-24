package com.example.HotelManagement.endpoints;

import com.example.HotelManagement.controllers.UserController;
import com.example.HotelManagement.models.User;
import com.example.HotelManagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testRegisterUser() throws Exception {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");

        doNothing().when(userService).saveUser(any(User.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register")
                        .param("name", user.getName())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        when(userService.validateUser("john.doe@example.com", "password")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .param("email", "john.doe@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkin-checkout"));

        verify(userService, times(1)).validateUser("john.doe@example.com", "password");
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        when(userService.validateUser("john.doe@example.com", "wrongpassword")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .param("email", "john.doe@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));

        verify(userService, times(1)).validateUser("john.doe@example.com", "wrongpassword");
    }
}
