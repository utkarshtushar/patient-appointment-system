package com.appointment.system.controller;

import com.appointment.system.dto.request.LoginRequest;
import com.appointment.system.dto.request.RegisterRequest;
import com.appointment.system.dto.response.AuthResponse;
import com.appointment.system.dto.response.UserResponse;
import com.appointment.system.entity.User;
import com.appointment.system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.authenticateAndGenerateToken(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
            
            // Get user details for response
            User user = authService.getUserByEmail(loginRequest.getEmail());
            UserResponse userResponse = new UserResponse(user);
            
            AuthResponse authResponse = new AuthResponse(token, userResponse);
            return ResponseEntity.ok(authResponse);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Validate doctor-specific fields
            if (registerRequest.getRole() == User.Role.DOCTOR) {
                if (registerRequest.getSpecialization() == null || registerRequest.getSpecialization().trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Specialization is required for doctor registration"));
                }
                if (registerRequest.getLicenseNumber() == null || registerRequest.getLicenseNumber().trim().isEmpty()) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "License number is required for doctor registration"));
                }
            }

            User user = new User();
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setPhoneNumber(registerRequest.getPhoneNumber());
            user.setRole(registerRequest.getRole());
            user.setSpecialization(registerRequest.getSpecialization());
            user.setLicenseNumber(registerRequest.getLicenseNumber());
            
            User savedUser = authService.registerUser(user);
            UserResponse userResponse = new UserResponse(savedUser);
            
            return ResponseEntity.ok(userResponse);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
