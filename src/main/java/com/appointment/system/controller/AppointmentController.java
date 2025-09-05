package com.appointment.system.controller;

import com.appointment.system.dto.request.BookAppointmentRequest;
import com.appointment.system.entity.Appointment;
import com.appointment.system.entity.AppointmentSlot;
import com.appointment.system.entity.User;
import com.appointment.system.service.AppointmentService;
import com.appointment.system.service.SlotGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private SlotGenerationService slotGenerationService;
    
    @PostMapping("/book")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Appointment> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request,
            Authentication authentication) {
        try {
            User patient = (User) authentication.getPrincipal();
            Appointment appointment = appointmentService.bookAppointment(
                patient.getId(), 
                request.getSlotId(), 
                request.getPatientNotes()
            );
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/patient/my-appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<Appointment>> getMyAppointments(Authentication authentication) {
        User patient = (User) authentication.getPrincipal();
        List<Appointment> appointments = appointmentService.getPatientAppointments(patient.getId());
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/doctor/my-appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(Authentication authentication) {
        User doctor = (User) authentication.getPrincipal();
        List<Appointment> appointments = appointmentService.getDoctorAppointments(doctor.getId());
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/slots/available")
    public ResponseEntity<List<AppointmentSlot>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AppointmentSlot> slots = slotGenerationService.getAvailableSlots(doctorId, startDate, endDate);
        return ResponseEntity.ok(slots);
    }
    
    @PutMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
