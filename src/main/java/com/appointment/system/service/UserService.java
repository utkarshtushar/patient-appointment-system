package com.appointment.system.service;

import com.appointment.system.entity.User;
import com.appointment.system.entity.DoctorSchedule;
import com.appointment.system.repository.UserRepository;
import com.appointment.system.repository.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private SlotGenerationService slotGenerationService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // If user is a doctor, create default schedule and generate slots
        if (savedUser.getRole() == User.Role.DOCTOR) {
            createDefaultDoctorSchedule(savedUser);
        }

        return savedUser;
    }

    private void createDefaultDoctorSchedule(User doctor) {
        // Create schedule for Monday to Friday, 9:00 AM to 5:00 PM with lunch break
        DayOfWeek[] workingDays = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                   DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};

        for (DayOfWeek day : workingDays) {
            DoctorSchedule schedule = new DoctorSchedule();
            schedule.setDoctor(doctor);
            schedule.setDayOfWeek(day);
            schedule.setStartTime(LocalTime.of(9, 0)); // 9:00 AM
            schedule.setEndTime(LocalTime.of(17, 0));  // 5:00 PM
            schedule.setSlotDurationMinutes(30);
            schedule.setBreakStartTime(LocalTime.of(12, 0)); // 12:00 PM (lunch break start)
            schedule.setBreakEndTime(LocalTime.of(13, 0));   // 1:00 PM (lunch break end)
            schedule.setIsActive(true);

            DoctorSchedule savedSchedule = doctorScheduleRepository.save(schedule);

            // Generate slots for the next 30 days for this schedule
            slotGenerationService.generateSlotsForDoctor(savedSchedule);
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllDoctors() {
        return userRepository.findActiveDoctors();
    }

    public List<User> findAllPatients() {
        return userRepository.findActivePatients();
    }

    public List<User> findDoctorsBySpecialization(String specialization) {
        return userRepository.findDoctorsBySpecialization(specialization);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
