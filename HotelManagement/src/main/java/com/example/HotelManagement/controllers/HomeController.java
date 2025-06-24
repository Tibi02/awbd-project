package com.example.HotelManagement.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home"; // Serves home.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // Serves register.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Serves login.html
    }
}
