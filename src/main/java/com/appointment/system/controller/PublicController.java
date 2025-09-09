package com.appointment.system.controller;

import com.appointment.system.entity.AppointmentSlot;
import com.appointment.system.service.SlotGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicController {

    @Autowired
    private SlotGenerationService slotGenerationService;

    @GetMapping("/slots/available")
    public ResponseEntity<List<AppointmentSlot>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<AppointmentSlot> availableSlots = slotGenerationService.getAvailableSlots(doctorId, startDate, endDate);
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            // Return empty list instead of error to ensure public access works
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> publicHealthCheck() {
        return ResponseEntity.ok("Public API endpoints are working!");
    }
}
