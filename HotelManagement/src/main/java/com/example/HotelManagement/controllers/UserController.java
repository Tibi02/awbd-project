package com.example.HotelManagement.controllers;

import com.example.HotelManagement.models.User;
import com.example.HotelManagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "register"; // sau trimiți mesaj JSON de eroare
        }
        userService.saveUser(user);
        return "redirect:/login";
    }


    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password) {
        if (userService.validateUser(email, password)) {
            return "redirect:/checkin-checkout"; // Successful login
        } else {
            return "redirect:/login?error"; // Redirect back to login with an error
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // Serves dashboard.html
    }

    @GetMapping("/checkin-checkout")
    public String checkinCheckoutPage() {
        return "checkin-checkout"; // Serves checkin-checkout.html
    }
}
