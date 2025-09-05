package com.appointment.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String home() {
        return "Patient Appointment System is running successfully!";
    }
    
    @GetMapping("/api/test")
    public String test() {
        return "API endpoints are working!";
    }
}
