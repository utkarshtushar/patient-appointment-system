package com.appointment.system.controller;

import com.appointment.system.dto.response.UserResponse;
import com.appointment.system.entity.User;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @RequestParam User.Role role,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            List<User> users;

            switch (currentUser.getRole()) {
                case PATIENT:
                    // Patients can only see doctors
                    if (role == User.Role.DOCTOR) {
                        users = userRepository.findByRole(User.Role.DOCTOR);
                    } else {
                        return ResponseEntity.forbidden().build();
                    }
                    break;

                case DOCTOR:
                    // Doctors can only see patients who have appointments with them
                    if (role == User.Role.PATIENT) {
                        // Get patient IDs who have appointments with this doctor
                        Set<Long> patientIds = appointmentRepository.findPatientIdsByDoctorId(currentUser.getId());
                        users = userRepository.findByIdInAndRole(patientIds, User.Role.PATIENT);
                    } else {
                        return ResponseEntity.forbidden().build();
                    }
                    break;

                case ADMIN:
                    // Admin can see doctors, patients, and other admins
                    users = userRepository.findByRole(role);
                    break;

                default:
                    return ResponseEntity.forbidden().build();
            }

            List<UserResponse> userResponses = users.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponses);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllDoctors() {
        try {
            List<User> doctors = userRepository.findByRole(User.Role.DOCTOR);
            List<UserResponse> doctorResponses = doctors.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(doctorResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/patients")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getPatients(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            List<User> patients;

            if (currentUser.getRole() == User.Role.DOCTOR) {
                // Doctor can only see their own patients
                Set<Long> patientIds = appointmentRepository.findPatientIdsByDoctorId(currentUser.getId());
                patients = userRepository.findByIdInAndRole(patientIds, User.Role.PATIENT);
            } else if (currentUser.getRole() == User.Role.ADMIN) {
                // Admin can see all patients
                patients = userRepository.findByRole(User.Role.PATIENT);
            } else {
                return ResponseEntity.forbidden().build();
            }

            List<UserResponse> patientResponses = patients.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(patientResponses);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllAdmins() {
        try {
            List<User> admins = userRepository.findByRole(User.Role.ADMIN);
            List<UserResponse> adminResponses = admins.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(adminResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
